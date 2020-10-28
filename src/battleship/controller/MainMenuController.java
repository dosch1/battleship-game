package battleship.controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import battleship.view.MainMenu;
import battleship.view.ViewManager;

public class MainMenuController {

	private ViewManager viewManager;
	private HumanPlayer humanPlayer;
	private ComputerPlayer computerPlayer;
	private SwingWorker <Void, Void> hostConnectionWorker;

	public MainMenuController(){
		viewManager = new ViewManager();
		setMainMenuActionListeners();
		setRulesWindowActionListener();
	}

	private void setMainMenuActionListeners(){
		MainMenu menu = viewManager.getMainMenu();

		menu.getRulesButton().addActionListener(e->{
			viewManager.getRulesWindow().setVisible(true);
		});

		//Networking Button Action Listener
		menu.getNetworkButton().addActionListener(e->{
			humanPlayer = new HumanPlayer(viewManager);
			int userAnswer = JOptionPane.showConfirmDialog(null, "Are you going to be hosting the game?", "Networking Dialog", JOptionPane.YES_NO_OPTION);

			if(userAnswer == JOptionPane.YES_OPTION) { 			//User Selected YES
				System.out.println("User Selected Yes: they are the host");
				String hostIP = humanPlayer.getIPAdrress();
				viewManager.getNetworkingHostWindow().getIPAddressOutsideLabel().setText("Your Outside IP Address is: " + hostIP);
				viewManager.getNetworkingHostWindow().setVisible(true);
				hostConnectionWorker = new SwingWorker <Void, Void>(){

					@Override
					protected Void doInBackground() throws Exception {
						humanPlayer.connectAsHost();
						return null;
					}
					
					public void done() {
						//TODO: Ship Placement Screen implementation here
						System.out.println("Connection Made!");
					}

				};
				hostConnectionWorker.execute();

			}else if (userAnswer == JOptionPane.NO_OPTION) { 	//User Selected NO
				System.out.println("User Selected No: they are client");
				setNetworkingClientActionListener();
				viewManager.getNetworkingClientWindow().setVisible(true);
			}
		});

		viewManager.getNetworkingHostWindow().addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				if(hostConnectionWorker != null) {
					hostConnectionWorker.cancel(true);
					if(humanPlayer != null) {
						humanPlayer.disconnect();
					}
					System.out.println("hostConnectionWorker canceled");
				}
			}
		});


		menu.getOnePlayerButton().addActionListener(e->{
			computerPlayer = new ComputerPlayer(viewManager);
			humanPlayer = new HumanPlayer(viewManager);
			//instantiate the correct players
			//set the player's view manager to this view manager
			viewManager.getGameScreen().setVisible(true);
		});
	}

	//for the closing the rules window
	private void setRulesWindowActionListener(){
		viewManager.getRulesWindow().getCloseButton().addActionListener(e->{
			viewManager.getRulesWindow().setVisible(false);
		});
	}

	private void setNetworkingClientActionListener() {
		viewManager.getNetworkingClientWindow().getConnectButton().addActionListener(e->{
			boolean isConnected = humanPlayer.connectAsClient(viewManager.getNetworkingClientWindow().getIPInput().getText());
			System.out.println("ServerStatus: " + isConnected);
			if(isConnected == false) {
				viewManager.getNetworkingClientWindow().getStatusLabel().setText("Couldn't Connect!");
			}else {
				JOptionPane.showMessageDialog(null, "Connection Successful!");
			}
		});
	}

	public void setHostIPLabel(String ipAddress) {
		viewManager.getNetworkingHostWindow().getIPAddressOutsideLabel().setText(ipAddress);
	}


}
