package net.todd.scorekeeper;

import java.util.List;

import net.todd.scorekeeper.data.Player;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class AddPlayersToGameView {
	private final ScrollView mainScrollView;
	private final TableLayout allPlayersTable;
	private final Context context;

	private boolean isCurrentPlayerSelected;
	private String currentPlayerId;

	private final ListenerManager doneButtonListenerManager = new ListenerManager();
	private final ListenerManager selectedPlayersChangedListenerManager = new ListenerManager();
	private final ListenerManager backPressedListenerManager = new ListenerManager();

	public AddPlayersToGameView(Context context) {
		this.context = context;

		mainScrollView = new ScrollView(context);
		mainScrollView.setFillViewport(true);
		mainScrollView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		LinearLayout mainView = new LinearLayout(context);
		mainView.setGravity(Gravity.CENTER_HORIZONTAL);
		LinearLayout.LayoutParams mainLayoutParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		mainLayoutParams.leftMargin = UIConstants.MARGIN_SIZE;
		mainLayoutParams.rightMargin = UIConstants.MARGIN_SIZE;
		mainView.setLayoutParams(mainLayoutParams);
		BackgroundUtil.setBackground(mainView);
		mainView.setOrientation(LinearLayout.VERTICAL);
		mainScrollView.addView(mainView);

		TextView title = new TextView(context);
		title.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		title.setText("Add Players");
		title.setTextSize(UIConstants.TEXT_TITLE_SIZE);
		title.setTextColor(UIConstants.TEXT_COLOR);
		title.setGravity(Gravity.CENTER_HORIZONTAL);
		mainView.addView(title);

		LinearLayout controlView = new LinearLayout(context);
		controlView.setGravity(Gravity.CENTER_HORIZONTAL);
		LinearLayout.LayoutParams controlViewLayouParams = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		controlViewLayouParams.leftMargin = UIConstants.MARGIN_SIZE;
		controlViewLayouParams.rightMargin = UIConstants.MARGIN_SIZE;
		controlView.setLayoutParams(controlViewLayouParams);
		controlView.setOrientation(LinearLayout.HORIZONTAL);
		mainView.addView(controlView);

		Button firstDoneButton = new Button(context);
		firstDoneButton.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				UIConstants.BUTTON_HEIGHT));
		firstDoneButton.setText("Done");
		firstDoneButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doneButtonListenerManager.notifyListeners();
			}
		});
		controlView.addView(firstDoneButton);

		allPlayersTable = new TableLayout(context);
		allPlayersTable.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));

		allPlayersTable.setGravity(Gravity.CENTER_HORIZONTAL);
		TableLayout.LayoutParams allPlayersTableLayoutParams = new TableLayout.LayoutParams(
				TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT);
		allPlayersTableLayoutParams.leftMargin = UIConstants.MARGIN_SIZE;
		allPlayersTableLayoutParams.rightMargin = UIConstants.MARGIN_SIZE;
		allPlayersTable.setLayoutParams(allPlayersTableLayoutParams);

		allPlayersTable.setColumnStretchable(1, true);
		mainView.addView(allPlayersTable);

		Button secondDoneButton = new Button(context);
		secondDoneButton.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				UIConstants.BUTTON_HEIGHT));
		secondDoneButton.setText("Done");
		secondDoneButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doneButtonListenerManager.notifyListeners();
			}
		});
		controlView.addView(secondDoneButton);
	}

	public View getView() {
		return mainScrollView;
	}

	public void setAllPlayers(List<Player> allPlayers) {
		allPlayersTable.removeAllViews();
		for (final Player player : allPlayers) {
			TableRow playerRow = new TableRow(context);
			playerRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
					TableRow.LayoutParams.WRAP_CONTENT));
			allPlayersTable.addView(playerRow);

			CheckBox playerSelection = new CheckBox(context);
			playerSelection.setChecked(player.isSelected());
			playerSelection.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					isCurrentPlayerSelected = isChecked;
					currentPlayerId = player.getId();
					selectedPlayersChangedListenerManager.notifyListeners();
				}
			});
			playerRow.addView(playerSelection);

			TextView playerName = new TextView(context);
			playerName.setText(player.getName());
			playerName.setTextSize(UIConstants.TEXT_NORMAL_SIZE);
			playerName.setTextColor(UIConstants.TEXT_COLOR);
			playerRow.addView(playerName);
		}
	}

	public void addSelectedPlayersChangedListener(Listener listener) {
		selectedPlayersChangedListenerManager.addListener(listener);
	}

	public boolean isCurrentPlayerSelected() {
		return isCurrentPlayerSelected;
	}

	public void addDoneButtonListener(final Listener listener) {
		doneButtonListenerManager.addListener(listener);
	}

	public String getCurrentPlayerId() {
		return currentPlayerId;
	}

	public void popupErrorMessage() {
		new AlertDialog.Builder(context).setMessage("You must select at least 2 players.")
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				}).show();
	}

	public void backPressed() {
		backPressedListenerManager.notifyListeners();
	}

	public void addBackPressedListener(Listener listener) {
		backPressedListenerManager.addListener(listener);
	}
}
