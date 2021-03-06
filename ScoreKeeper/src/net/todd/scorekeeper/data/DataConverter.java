package net.todd.scorekeeper.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.todd.scorekeeper.ObjectSerializerPersistor;
import net.todd.scorekeeper.Persistor;
import android.content.Context;

@SuppressWarnings("deprecation")
public class DataConverter {
	public static class ClassConversionBean {
		private final Class<?> fromClass;
		private final Class<?> toClass;

		public ClassConversionBean(Class<?> fromClass, Class<?> toClass) {
			this.fromClass = fromClass;
			this.toClass = toClass;
		}

		public Class<?> getFromClass() {
			return fromClass;
		}

		public Class<?> getToClass() {
			return toClass;
		}

		@Override
		public String toString() {
			return fromClass.getName() + " => " + toClass.getName();
		}
	}

	private final Context context;
	private final List<ClassConversionBean> knownConversionBeans;

	public DataConverter(Context context, List<ClassConversionBean> knownConversionBeans) {
		this.context = context;
		this.knownConversionBeans = knownConversionBeans;
	}

	public <T, S> void convertData(Class<T> fromClass, Class<S> toClass) {
		Persistor<T> source = ObjectSerializerPersistor.create(fromClass, context);
		Persistor<S> target = XmlPersistor.create(toClass, context);
		List<T> originalObjects = source.load();
		if (!originalObjects.isEmpty()) {
			List<S> translatedObjects = new ArrayList<S>();
			for (T original : originalObjects) {
				try {
					Object translated = translateObject(original);
					translatedObjects.add(toClass.cast(translated));
				} catch (Exception e) {
					throw new RuntimeException("Data conversion failed", e);
				}
			}
			target.persist(translatedObjects);
			source.persist(new ArrayList<T>());
		}
	}

	private Object translateObject(Object original) throws Exception {
		Class<?> translatedClass = findTranslatedClass(original);

		Object translated = null;
		if (translatedClass == null) {
			translated = original;
		} else {
			translated = translatedClass.newInstance();
			for (Field originalField : original.getClass().getDeclaredFields()) {
				if (!"serialVersionUID".equals(originalField.getName())) {
					Field tranlsatedField;
					try {
						tranlsatedField = translated.getClass().getDeclaredField(
								originalField.getName());
						originalField.setAccessible(true);
						tranlsatedField.setAccessible(true);
						Object originalValue = translateField(originalField.getType(),
								originalField.get(original));
						tranlsatedField.set(translated, originalValue);
					} catch (NoSuchFieldException e) {
					}
				}
			}
		}

		return translated;
	}

	private Class<?> findTranslatedClass(Object original) {
		Class<?> translatedClass = null;
		for (ClassConversionBean classConversionBean : knownConversionBeans) {
			Class<?> fromClass = classConversionBean.getFromClass();
			if (fromClass.isInstance(original)) {
				translatedClass = classConversionBean.toClass;
				break;
			}
		}
		return translatedClass;
	}

	private Object translateField(Class<?> originalType, Object originalValue) throws Exception {
		Object translatedValue;
		if (Collection.class.isAssignableFrom(originalType)) {
			translatedValue = new ArrayList<Object>();
			for (Object originalElement : Collection.class.cast(originalValue)) {
				Object translatedElement = translateObject(originalElement);
				@SuppressWarnings("unchecked")
				Collection<Object> translatedCollection = (Collection<Object>) translatedValue;
				translatedCollection.add(translatedElement);
			}
		} else {
			translatedValue = translateObject(originalValue);
		}
		return translatedValue;
	}
}
