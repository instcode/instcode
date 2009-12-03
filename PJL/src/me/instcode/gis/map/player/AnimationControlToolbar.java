package me.instcode.gis.map.player;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

import me.instcode.gis.Constants;

/**
 * Control an player via some GUI's components.
 */
public class AnimationControlToolbar extends JToolBar implements ActionListener, AnimationListener {
	private static final long serialVersionUID = 1L;
	
	protected static final String PLAY_PAUSE_COMMAND = "playpause";
	protected static final String STOP_COMMAND = "stop";
	protected static final String STEP_FORWARD_COMMAND = "fw";
	protected static final String STEP_BACKWARD_COMMAND = "bw";

	protected JButton playPauseButton;
	protected JButton stopButton;
	protected JButton stepBackButton;
	protected JButton stepForwardButton;

	protected AnimationPlayer animationPlayer;

	protected ImageIcon playIcon;
	protected ImageIcon pauseIcon;

	public AnimationControlToolbar() {
		super();

		playIcon = Constants.createIcon(Constants.PLAY_ICON);
		pauseIcon = Constants.createIcon(Constants.PAUSE_ICON);

		playPauseButton = new JButton();
		playPauseButton.setIcon(playIcon);
		playPauseButton.setPressedIcon(pauseIcon);
		playPauseButton.setActionCommand(PLAY_PAUSE_COMMAND);
		playPauseButton.setMnemonic(KeyEvent.VK_SPACE);
		playPauseButton.addActionListener(this);

		stopButton = createButton(Constants.STOP_ICON, STOP_COMMAND);
		stepForwardButton = createButton(Constants.STEP_FORWARD_ICON, STEP_FORWARD_COMMAND);
		stepBackButton = createButton(Constants.STEP_BACKWARD_ICON, STEP_BACKWARD_COMMAND);

		add(stepBackButton);
		add(stopButton);
		add(playPauseButton);
		add(stepForwardButton);
	}

	public void attach(AnimationPlayer animationPlayer) {
		this.animationPlayer = animationPlayer;
	}
	
	private JButton createButton(String icon, String command) {
		JButton button = new JButton();
		button.setIcon(Constants.createIcon(icon));
		button.setActionCommand(command);
		button.addActionListener(this);
		return button;
	}

	/**
	 * Invoked when an action occurs.
	 */
	public void actionPerformed(ActionEvent e) {
		if (PLAY_PAUSE_COMMAND.equals(e.getActionCommand())) {
			if (animationPlayer.isPlaying()) {
				animationPlayer.pause();
			}
			else {
				animationPlayer.play();
			}
		}
		else if (STOP_COMMAND.equals(e.getActionCommand())) {
			animationPlayer.stop();
		}
		else if (STEP_FORWARD_COMMAND.equals(e.getActionCommand())) {
			animationPlayer.stepForward();
		}
		else if (STEP_BACKWARD_COMMAND.equals(e.getActionCommand())) {
			animationPlayer.stepBackward();
		}
	}

	public void notifyEvent(int event) {
		switch (event) {
		case AnimationPlayer.PLAYER_PAUSED_EVENT:
			stepForwardButton.setEnabled(false);
			stepBackButton.setEnabled(false);
			playPauseButton.setIcon(playIcon);
			break;
			
		case AnimationPlayer.PLAYER_STOPPED_EVENT:
			stepForwardButton.setEnabled(true);
			stepBackButton.setEnabled(true);
			playPauseButton.setIcon(playIcon);
			break;
			
		case AnimationPlayer.PLAYER_START_PLAYING_EVENT:
			stepForwardButton.setEnabled(false);
			stepBackButton.setEnabled(false);
			playPauseButton.setIcon(pauseIcon);
			break;
		}
	}
}
