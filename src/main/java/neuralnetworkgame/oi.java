package neuralnetworkgame;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Panel;

import javax.swing.SwingConstants;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class oi extends JFrame implements ActionListener {
	String a = "0";
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	oi frame;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					oi frame = new oi();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public oi() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 100, 100);
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		main ob = new main();
		frame.getContentPane().add(ob);
		ob.setBounds(0, 0, contentPane.getHeight(), contentPane.getWidth());
	}

	public int getHeight() {
		return contentPane.getHeight();
	}

	public int getWidth() {
		return contentPane.getWidth();
	}

}
