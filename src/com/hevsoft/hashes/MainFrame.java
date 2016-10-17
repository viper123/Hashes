package com.hevsoft.hashes;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.text.MessageFormat;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.hevsoft.hashes.calculator.HashCalculator;
import com.hevsoft.hashes.calculator.HashCalculatorListener;
import com.hevsoft.hashes.format.HashFormatter;

import javax.swing.JCheckBox;
import javax.swing.JScrollPane;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtPath;
	private JButton btnBrowse;
	private JComboBox<String> comboBox;
	private JCheckBox chckbxAll;
	private JButton btnProcess;
	private JTextPane txtResultHash;
	private JTextPane txtExternalHash;
	
	private PreferencesWrapper preferences;
	private int lastAlgIndex = 0;
	private File currentFile;
	private JScrollPane scrollPane_1;
	private JScrollPane svHashResult;

	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					if(args!= null && args.length>0){
						frame.currentFile =new File(args[0]);
						frame.onCurrentFileChanged(frame.currentFile);
					}
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
	public MainFrame() {
		setTitle("Hashes");
		setResizable(false);
		init();
		setUpUI();
		loadData();
		setListener();
	}
	
	private void init(){
		preferences = new PreferencesWrapper();
	}
	
	private void setUpUI(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 422);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblLocation = new JLabel("Path:");
		lblLocation.setBounds(10, 11, 37, 14);
		contentPane.add(lblLocation);
		
		txtPath = new JTextField();
		txtPath.setBounds(51, 8, 274, 20);
		contentPane.add(txtPath);
		txtPath.setColumns(10);
		
		btnProcess = new JButton("Process");
		btnProcess.setBounds(10, 51, 89, 23);
		contentPane.add(btnProcess);
		
		btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(335, 7, 89, 23);
		contentPane.add(btnBrowse);
		
		comboBox = new JComboBox<>();
		comboBox.setBounds(219, 52, 131, 20);
		contentPane.add(comboBox);
		
		JLabel lblAlgorithm = new JLabel("Algorithm:");
		lblAlgorithm.setBounds(154, 55, 62, 14);
		contentPane.add(lblAlgorithm);
		
		JLabel lblTestAgainst = new JLabel("External Hash:");
		lblTestAgainst.setBounds(10, 262, 89, 14);
		contentPane.add(lblTestAgainst);
		
		chckbxAll = new JCheckBox("All");
		chckbxAll.setBounds(360, 51, 64, 23);
		contentPane.add(chckbxAll);
		
		svHashResult = new JScrollPane();
		svHashResult.setBounds(10, 85, 414, 166);
		contentPane.add(svHashResult);
		
		txtResultHash = new JTextPane();
		svHashResult.setViewportView(txtResultHash);
		txtResultHash.setEditable(false);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 287, 414, 91);
		contentPane.add(scrollPane_1);
		
		txtExternalHash = new JTextPane();
		scrollPane_1.setViewportView(txtExternalHash);
		
	}
	
	private void setListener(){
		btnBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openChooseFileDialog();
			}
		});
		comboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				int selectedIndex = comboBox.getSelectedIndex();
				if(arg0.getStateChange() == ItemEvent.DESELECTED){
					return;
				}
				if(selectedIndex == lastAlgIndex){
					return;
				}
				lastAlgIndex = selectedIndex;
				SupportedAlgorithms alg = SupportedAlgorithms.fromIndex(selectedIndex);
				if(alg != null){
					onAlgorithmChanged(alg);
				}
			}
		});
		chckbxAll.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				boolean checked = chckbxAll.isSelected();
				comboBox.setEnabled(!checked);
			}
		});
		btnProcess.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SupportedAlgorithms [] algs = chckbxAll.isSelected()?SupportedAlgorithms.values():
					new SupportedAlgorithms[]{SupportedAlgorithms.fromIndex(comboBox.getSelectedIndex())};
				currentFile = new File(txtPath.getText());
				preferences.setLastPath(currentFile.getAbsolutePath());
				computeHashForFile(currentFile, algs);
			}
		});
	}
	
	private void openChooseFileDialog(){
		JFileChooser fileChooser = new JFileChooser();
		String lastPath = preferences.getLastPath();
		if(lastPath != null){
			fileChooser.setCurrentDirectory(new File(lastPath));
		}
		int res = fileChooser.showOpenDialog(this);
		if(res == JFileChooser.APPROVE_OPTION){
			File file =  fileChooser.getSelectedFile();
			if(file != null){
				onCurrentFileChanged(file);
			}
		}
	}
	
	private void onCurrentFileChanged(File file){
		txtPath.setText(file.getAbsolutePath());
		preferences.setLastPath(file.getAbsolutePath());
	}
	
	private void onAlgorithmChanged(SupportedAlgorithms alg){
		preferences.setLastAlg(alg.ordinal());
	}
	
	private void loadData(){
		loadPathData();
		loadAlgorithms();
	}
	
	private void loadPathData(){
		if(currentFile == null){
			String lastPath = preferences.getLastPath();
			if(lastPath!= null){
				currentFile = new File(lastPath);
			}
		}
		if(currentFile != null){
			txtPath.setText(currentFile.getAbsolutePath());
		}
	}
	
	private void loadAlgorithms(){
		for(SupportedAlgorithms alg:SupportedAlgorithms.values()){
			comboBox.addItem(alg.name());
		}
		int lastAlg = preferences.getLastAlg();
		if(lastAlg != -1){
			comboBox.setSelectedItem(lastAlg);
		}
	}
	
	private void computeHashForFile(File file,SupportedAlgorithms [] algs){
		btnProcess.setEnabled(false);
		HashCalculator calculator = HashCalculator.Factory.INSTANCE.getHashCalculator();
		calculator.setListener(new HashCalculatorListener() {
			
			@Override
			public void onHashComputed(SupportedAlgorithms alg, byte[] data) {
				String myHash = HashFormatter.toString(data);
				updateHashCompleted(alg.name(),myHash );
				if(alg.ordinal() == comboBox.getSelectedIndex() && isHashToCompare()){
					String externalHash = txtExternalHash.getText();
					JOptionPane.showMessageDialog(MainFrame.this, externalHash.equalsIgnoreCase(myHash)?"Hash OK":"Hash fail");
				}
			}
			
			@Override
			public void onFinish() {
				btnProcess.setEnabled(true);
			}
			
			@Override
			public void onException(Exception e) {
				JOptionPane.showMessageDialog(MainFrame.this, "Erorr: "+e.getMessage());
				btnProcess.setEnabled(true);
			}
			
			@Override
			public void onCanceled() {
				btnProcess.setEnabled(true);
			}
		});
		calculator.computeHash(file, algs);
	}
	
	private void updateHashCompleted(String alg,String hash){
		txtResultHash.setText(txtResultHash.getText()+
				"\n"+alg+" : "+hash);
	}
	
	private boolean isHashToCompare(){
		return txtExternalHash.getText() != null && txtExternalHash.getText().trim().length()>0;
	}
}
