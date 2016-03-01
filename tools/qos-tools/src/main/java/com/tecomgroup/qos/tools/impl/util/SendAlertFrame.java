/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl.util;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

import com.tecomgroup.qos.communication.message.AlertMessage;
import com.tecomgroup.qos.communication.message.AlertMessage.AlertAction;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.SpecificReason;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.exception.ServiceException;
import com.tecomgroup.qos.tools.impl.SendAlert;
import com.tecomgroup.qos.util.ConfigurationUtil;
import com.tecomgroup.qos.util.SharedModelConfiguration;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * Графический интерфейс для посылки алёртов
 * 
 * @author novohatskiy.r
 * 
 */
@SuppressWarnings("serial")
public class SendAlertFrame extends JFrame {

	private class AddSaveButtonActionListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			AlertAction action = null;
			final Enumeration<AbstractButton> buttons = actions.getElements();
			while (buttons.hasMoreElements()) {
				final AbstractButton button = buttons.nextElement();
				if (button.isSelected()) {
					action = AlertAction.valueOf(button.getText());
					break;
				}
			}
			final String selectedParameterName = (String) parameterComboBox
					.getSelectedItem();
			Source source = null;
			Source originator = null;
			switch ((Source.Type) sourceTypeComboBox.getSelectedItem()) {
				case TASK :
					source = Source.getTaskSource(sourceField.getText());
					originator = Source
							.getPolicySource(SharedModelConfiguration
									.createPolicyKey(SharedModelConfiguration
											.createPolicyTemplateKey(agentKey,
													selectedParameterName),
											source.getKey(),
											selectedParameterName));
					break;
				case AGENT :
					// FIXME when it will be supportable.
					throw new ServiceException("Unsupported source type "
							+ sourceTypeComboBox.getSelectedItem());
				default :
					throw new ServiceException("Unsupported source type "
							+ sourceTypeComboBox.getSelectedItem());
			}

			String settings = settingsField.getText();
			if (!SimpleUtils.isNotNullAndNotEmpty(settings)) {
				settings = ConfigurationUtil.PARAMETER_NAME
						+ ConfigurationUtil.NAME_VALUE_SEPARATOR
						+ selectedParameterName;
			}
			final AlertMessage alert = SendAlert.createAlert(action,
					alertTypeField.getText(), source, originator, settings,
					(PerceivedSeverity) perceivedSeverityComboBox
							.getSelectedItem(),
					(SpecificReason) specificReasonComboBox.getSelectedItem());

			final int rowSelected = alertTable.getSelectedRow();
			if (rowSelected == 0) {
				addAlert(alert);
			} else {
				alerts.remove(rowSelected - 1);
				alerts.add(rowSelected - 1, alert);
				alertTable.tableChanged(new TableModelEvent(alertTable
						.getModel(), rowSelected, rowSelected,
						TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
			}
		}
	}

	private class AlertListSelectionListener implements ListSelectionListener {
		@Override
		public void valueChanged(final ListSelectionEvent e) {
			final int selectedRow = alertTable.getSelectedRow();
			if (selectedRow < 0) {
				return;
			} else if (selectedRow == 0) {
				restoreDefaults();
				addSaveButton.setText(ADD_ALERT_LABEL);
			} else {
				alertSelected(alerts.get(selectedRow - 1));
				addSaveButton.setText(SAVE_ALERT_LABEL);
			}
		}
	}

	private class AlertTableModel extends AbstractTableModel {
		@Override
		public int getColumnCount() {
			return 8;
		}

		@Override
		public String getColumnName(final int c) {
			String result = "";
			switch (c) {
				case 1 :
					result = ACTION_LABEL;
					break;
				case 2 :
					result = ALERT_TYPE_LABEL;
					break;
				case 3 :
					result = SOURCE_TYPE_LABEL;
					break;
				case 4 :
					result = SOURCE_LABEL;
					break;
				case 5 :
					result = SETTINGS_LABEL;
					break;
				case 6 :
					result = PERCEIVED_SEVERITY_LABEL;
					break;
				case 7 :
					result = SPECIFIC_REASON_LABEL;
					break;
			}
			return result;
		}

		@Override
		public int getRowCount() {
			return alerts.size() + 1;
		}

		@Override
		public Object getValueAt(int rowIndex, final int columnIndex) {
			if (rowIndex == 0) {
				switch (columnIndex) {
					case 0 :
						return false;
					case 1 :
						return NEW_LABEL;
				}
			} else {
				rowIndex--;
				final AlertMessage alert = alerts.get(rowIndex);
				switch (columnIndex) {
					case 0 :
						return alertsToSendIndexes.contains(rowIndex);
					case 1 :
						return alert.getAction();
					case 2 :
						return alert.getAlert().getAlertType().getName();
					case 3 :
						return alert.getAlert().getSource().getType();
					case 4 :
						return alert.getAlert().getSource();
					case 5 :
						return alert.getAlert().getSettings();
					case 6 :
						return alert.getAlert().getPerceivedSeverity();
					case 7 :
						return alert.getAlert().getSpecificReason();
				}
			}
			return null;
		}
		@Override
		public boolean isCellEditable(final int rowIndex, final int columnIndex) {
			if (columnIndex == 0) {
				return true;
			}
			return false;
		}

		@Override
		public void setValueAt(final Object aValue, int rowIndex,
				final int columnIndex) {
			if (rowIndex == 0) {
				return;
			}
			rowIndex--;
			switch (columnIndex) {
				case 0 :
					final boolean val = (Boolean) aValue;
					final Integer integerValue = new Integer(rowIndex);
					if (val) {
						if (!alertsToSendIndexes.contains(integerValue)) {
							alertsToSendIndexes.add(integerValue);
						}
					} else {
						alertsToSendIndexes.remove(integerValue);
					}
				case 1 :
				case 2 :
				case 3 :
				case 4 :
				case 5 :
				case 6 :
			}
		}
	}

	private class CheckBoxHeader extends JCheckBox
			implements
				TableCellRenderer,
				MouseListener {

		protected int column;
		protected boolean mousePressed = false;

		public CheckBoxHeader(final ItemListener itemListener) {
			addItemListener(itemListener);
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table,
				final Object value, final boolean isSelected,
				final boolean hasFocus, final int row, final int column) {
			if (table != null) {
				final JTableHeader header = table.getTableHeader();
				if (header != null) {
					this.setForeground(header.getForeground());
					this.setBackground(header.getBackground());
					header.addMouseListener(this);
				}
			}
			setColumn(column);
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));
			return this;
		}

		@Override
		public void mouseClicked(final MouseEvent e) {
			if (mousePressed) {
				mousePressed = false;
				final JTableHeader header = (JTableHeader) (e.getSource());
				final JTable tableView = header.getTable();
				final TableColumnModel columnModel = tableView.getColumnModel();
				final int viewColumn = columnModel.getColumnIndexAtX(e.getX());
				final int column = tableView
						.convertColumnIndexToModel(viewColumn);

				if (viewColumn == this.column && e.getClickCount() == 1
						&& column != -1) {
					doClick();
					header.repaint();
				}
			}
		}
		@Override
		public void mouseEntered(final MouseEvent e) {
		}

		@Override
		public void mouseExited(final MouseEvent e) {
		}

		@Override
		public void mousePressed(final MouseEvent e) {
			mousePressed = true;
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
		}

		protected void setColumn(final int column) {
			this.column = column;
		}

	}

	private class CheckBoxItemListener implements ItemListener {
		@Override
		public void itemStateChanged(final ItemEvent e) {
			if (!(e.getSource() instanceof AbstractButton)) {
				return;
			}
			final boolean checked = e.getStateChange() == ItemEvent.SELECTED;
			for (int x = 0, y = alertTable.getRowCount(); x < y; x++) {
				alertTable.setValueAt(new Boolean(checked), x, 0);
			}
			alertTable
					.tableChanged(new TableModelEvent(alertTable.getModel(), 1,
							alertTable.getRowCount() - 1, 0,
							TableModelEvent.UPDATE));
		}
	}

	private class DuplicateAlertListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			int selectedRow = alertTable.getSelectedRow();
			if (selectedRow > 0) {
				addAlert(alerts.get(--selectedRow));
			}
		}
	}

	private class OpenFileListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			final int retVal = fileChooser.showOpenDialog(SendAlertFrame.this);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				openFile(fileChooser.getSelectedFile());
			}
		}
	}

	private class PopupListener extends MouseAdapter {
		private void handleMouseEvent(final MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				final Point p = e.getPoint();
				final int rowNumber = alertTable.rowAtPoint(p);
				final ListSelectionModel model = alertTable.getSelectionModel();
				model.setSelectionInterval(rowNumber, rowNumber);
				if (e.isPopupTrigger()) {
					final int selectedRow = alertTable.getSelectedRow();
					if (selectedRow != 0) {
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		}

		@Override
		public void mousePressed(final MouseEvent e) {
			handleMouseEvent(e);
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			handleMouseEvent(e);
		}
	}

	private class SaveFileListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			final int retVal = fileChooser.showSaveDialog(SendAlertFrame.this);
			if (retVal == JFileChooser.APPROVE_OPTION) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						provider.writeAlertsToFile(
								fileChooser.getSelectedFile(), alerts);
					}
				});
			}
		}
	}

	private class SendAllAlertsListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					provider.sendAlerts(alerts);
				}
			});
		}
	}

	private class SendSelectedAlertsListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			final List<AlertMessage> toSend = new ArrayList<AlertMessage>();
			for (final int i : alertsToSendIndexes) {
				toSend.add(alerts.get(i));
			}
			executor.execute(new Runnable() {
				@Override
				public void run() {
					provider.sendAlerts(toSend);
				}
			});
		}
	}

	public static final String FRAME_TITLE = "Send Alert";
	public static final String ACTION_LABEL = "Action";
	public static final String ALERT_TYPE_LABEL = "Alert type";
	public static final String SOURCE_LABEL = "Source";
	public static final String SOURCE_TYPE_LABEL = "Source type";
	public static final String PARAMETER_LABEL = "Parameter";
	public static final String PERCEIVED_SEVERITY_LABEL = "Perceived severity";
	public static final String SPECIFIC_REASON_LABEL = "Specific reason";
	public static final String ADD_ALERT_LABEL = "Add Alert";
	public static final String SAVE_ALERT_LABEL = "Save Alert";
	public static final String FILE_LABEL = "File";
	public static final String SEND_LABEL = "Send";
	public static final String OPEN_LABEL = "Open...";
	public static final String SAVE_AS_LABEL = "Save as...";
	public static final String SEND_SELECTED_ALERTS_LABEL = "Send selected alerts";
	public static final String SEND_ALL_LABEL = "Send all";
	public static final String NEW_LABEL = "new...";
	public static final String DELETE_LABEL = "Delete";
	public static final String DUPLICATE_LABEL = "Duplicate";
	public static final String SETTINGS_LABEL = "Settings";

	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem openFileMenuItem;
	private JMenuItem saveFileMenuItem;
	private JMenu sendMenu;
	private JMenuItem sendSelectedAlertsMenuItem;
	private JMenuItem sendAllMenuItem;
	private JPanel propertiesPanel;
	private JPanel actionsPanel;
	private ButtonGroup actions;
	private JTextField alertTypeField;
	private JComboBox<Source.Type> sourceTypeComboBox;
	private JComboBox<String> parameterComboBox;
	private JTextField sourceField;
	private JTextField settingsField;
	private JComboBox<MAlertType.PerceivedSeverity> perceivedSeverityComboBox;
	private JComboBox<MAlertType.SpecificReason> specificReasonComboBox;
	private JButton addSaveButton;
	private JTable alertTable;
	private JScrollPane alertScrollPane;
	private JTextArea resultArea;
	private JScrollPane resultScrollPane;
	private JPopupMenu popup;
	private JMenuItem deletePopupItem;
	private JMenuItem duplicatePopupItem;
	private JFileChooser fileChooser;

	private List<AlertMessage> alerts;
	private final List<Integer> alertsToSendIndexes;

	private AlertAction defaultAction;
	private String agentKey;
	private String defaultAlertType;
	private Source defaultSource;
	private PerceivedSeverity defaultPerceivedSeverity;
	private SpecificReason defaultSpecificReason;
	private String defaultSettings;

	private final SendAlert provider;
	private final ExecutorService executor;

	public SendAlertFrame(final SendAlert provider) {
		super();
		this.provider = provider;
		executor = Executors.newSingleThreadExecutor();
		alerts = new ArrayList<AlertMessage>();
		alertsToSendIndexes = new ArrayList<Integer>();
		initializeUI();
	}

	private void addAlert(final AlertMessage alert) {
		alerts.add(alert);
		final int row = alerts.size();
		alertTable.tableChanged(new TableModelEvent(alertTable.getModel(), row,
				row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

	private void alertSelected(final AlertMessage alertMessage) {
		setAction(alertMessage.getAction());
		setAlertType(alertMessage.getAlert().getAlertType().getName());
		setSourceType(alertMessage.getAlert().getSource().getType());
		setSource(alertMessage.getAlert().getSource());
		setSettings(alertMessage.getAlert().getSettings());
		setPerceivedSeverity(alertMessage.getAlert().getPerceivedSeverity());
		setSpecificReason(alertMessage.getAlert().getSpecificReason());
		setParameter(alertMessage.getAlert().getAlertType().getName());
	}

	@SuppressWarnings("rawtypes")
	private <T extends Enum> JComboBox<T> createComboBox(
			final Class<T> enumeration) {
		final JComboBox<T> comboBox = new JComboBox<>();
		for (final T e : enumeration.getEnumConstants()) {
			comboBox.addItem(e);
		}
		return comboBox;
	}

	private void deleteSelectedAlert() {
		final int selectedRow = alertTable.getSelectedRow();
		if (selectedRow > 0) {
			final int index = selectedRow - 1;
			alerts.remove(index);
			alertsToSendIndexes.remove(new Integer(index));
			alertTable.tableChanged(new TableModelEvent(alertTable.getModel(),
					selectedRow, selectedRow, TableModelEvent.ALL_COLUMNS,
					TableModelEvent.DELETE));
		}
	}

	private void initializeUI() {
		// Frame
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(FRAME_TITLE);
		setSize(900, 395);
		setLocationRelativeTo(getRootPane());
		setResizable(false);
		setLayout(new GridBagLayout());

		// Menu bar
		menuBar = new JMenuBar();
		fileMenu = new JMenu(FILE_LABEL);
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);

		openFileMenuItem = new JMenuItem(OPEN_LABEL);
		openFileMenuItem.setMnemonic(KeyEvent.VK_O);
		openFileMenuItem.addActionListener(new OpenFileListener());
		fileMenu.add(openFileMenuItem);

		saveFileMenuItem = new JMenuItem(SAVE_AS_LABEL);
		saveFileMenuItem.setMnemonic(KeyEvent.VK_S);
		saveFileMenuItem.addActionListener(new SaveFileListener());
		fileMenu.add(saveFileMenuItem);

		sendMenu = new JMenu(SEND_LABEL);
		sendMenu.setMnemonic(KeyEvent.VK_S);
		menuBar.add(sendMenu);

		sendSelectedAlertsMenuItem = new JMenuItem(SEND_SELECTED_ALERTS_LABEL);
		sendSelectedAlertsMenuItem.setMnemonic(KeyEvent.VK_S);
		sendSelectedAlertsMenuItem
				.addActionListener(new SendSelectedAlertsListener());
		sendMenu.add(sendSelectedAlertsMenuItem);

		sendAllMenuItem = new JMenuItem(SEND_ALL_LABEL);
		sendAllMenuItem.setMnemonic(KeyEvent.VK_A);
		sendAllMenuItem.addActionListener(new SendAllAlertsListener());
		sendMenu.add(sendAllMenuItem);

		final GridBagConstraints frameGBC = new GridBagConstraints();
		frameGBC.gridx = 0;
		frameGBC.gridy = 0;
		frameGBC.gridwidth = 5;
		frameGBC.gridheight = 1;
		frameGBC.weighty = 5;
		frameGBC.fill = GridBagConstraints.BOTH;

		add(menuBar, frameGBC);

		frameGBC.gridx = 0;
		frameGBC.gridy = 1;
		frameGBC.fill = GridBagConstraints.BOTH;
		frameGBC.gridwidth = 1;
		frameGBC.weighty = 1;

		final Insets insets = new Insets(3, 3, 3, 3);

		frameGBC.insets = insets;

		// Properties panel
		propertiesPanel = new JPanel();
		propertiesPanel.setLayout(new GridBagLayout());
		propertiesPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		add(propertiesPanel, frameGBC);

		final GridBagConstraints leftGBC = new GridBagConstraints();
		leftGBC.gridx = 0;
		leftGBC.gridy = 0;
		leftGBC.anchor = GridBagConstraints.WEST;
		leftGBC.weightx = 0;

		final GridBagConstraints rightGBC = new GridBagConstraints();
		rightGBC.gridx = 1;
		rightGBC.gridy = 0;
		rightGBC.fill = GridBagConstraints.HORIZONTAL;

		leftGBC.insets = insets;
		rightGBC.insets = insets;

		propertiesPanel.add(new JLabel(ACTION_LABEL + ":"), leftGBC);

		actionsPanel = new JPanel();
		actionsPanel.setAlignmentX(JPanel.LEFT_ALIGNMENT);
		actionsPanel.setLayout(new GridLayout(0, 2));
		propertiesPanel.add(actionsPanel, rightGBC);

		// Action controls
		actions = new ButtonGroup();
		for (final AlertAction action : AlertAction.values()) {
			final JRadioButton rb = new JRadioButton(action.name());
			actions.add(rb);
			actionsPanel.add(rb);
		}

		leftGBC.gridy = 1;
		rightGBC.gridy = 1;

		// Alert Type controls
		propertiesPanel.add(new JLabel(ALERT_TYPE_LABEL + ":"), leftGBC);
		alertTypeField = new JTextField();
		alertTypeField.setSize(200, 20);
		alertTypeField.setEditable(false);
		propertiesPanel.add(alertTypeField, rightGBC);

		leftGBC.gridy = 2;
		rightGBC.gridy = 2;

		// Source Type controls
		propertiesPanel.add(new JLabel(SOURCE_TYPE_LABEL + ":"), leftGBC);
		sourceTypeComboBox = new JComboBox<Source.Type>();
		// sourceTypeComboBox.addItem(Source.Type.AGENT);
		sourceTypeComboBox.addItem(Source.Type.TASK);
		propertiesPanel.add(sourceTypeComboBox, rightGBC);

		leftGBC.gridy = 3;
		rightGBC.gridy = 3;

		// Source controls
		propertiesPanel.add(new JLabel(SOURCE_LABEL + ":"), leftGBC);
		sourceField = new JTextField();
		propertiesPanel.add(sourceField, rightGBC);

		leftGBC.gridy = 4;
		rightGBC.gridy = 4;

		// Parameters
		propertiesPanel.add(new JLabel(PARAMETER_LABEL + ":"), leftGBC);
		parameterComboBox = new JComboBox<String>();
		for (final String parameter : SharedModelConfiguration.IT09A_MODULE_PARAMETER_LIST) {
			parameterComboBox.addItem(parameter);
		}
		parameterComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				alertTypeField
						.setText(SharedModelConfiguration.IT09A_ALERT_TYPE_PREFIX
								+ parameterComboBox.getSelectedItem());
			}
		});
		propertiesPanel.add(parameterComboBox, rightGBC);

		leftGBC.gridy = 5;
		rightGBC.gridy = 5;

		// Settings controls
		propertiesPanel.add(new JLabel(SETTINGS_LABEL + ":"), leftGBC);
		settingsField = new JTextField();
		propertiesPanel.add(settingsField, rightGBC);

		leftGBC.gridy = 6;
		rightGBC.gridy = 6;

		// Perceived Severity controls
		propertiesPanel
				.add(new JLabel(PERCEIVED_SEVERITY_LABEL + ":"), leftGBC);
		perceivedSeverityComboBox = createComboBox(PerceivedSeverity.class);
		propertiesPanel.add(perceivedSeverityComboBox, rightGBC);

		leftGBC.gridy = 7;
		rightGBC.gridy = 7;

		// Specific Reason controls
		propertiesPanel.add(new JLabel(SPECIFIC_REASON_LABEL + ":"), leftGBC);
		specificReasonComboBox = createComboBox(SpecificReason.class);
		propertiesPanel.add(specificReasonComboBox, rightGBC);

		// Add button
		addSaveButton = new JButton(ADD_ALERT_LABEL);
		rightGBC.gridy = 8;
		propertiesPanel.add(addSaveButton, rightGBC);

		// Alert table
		alertTable = new JTable(new AlertTableModel());
		alertScrollPane = new JScrollPane(alertTable);

		alertTable.getColumnModel().getColumn(0)
				.setCellEditor(alertTable.getDefaultEditor(Boolean.class));
		alertTable.getColumnModel().getColumn(0)
				.setCellRenderer(alertTable.getDefaultRenderer(Boolean.class));
		alertTable.getColumnModel().getColumn(0).setPreferredWidth(20);
		alertTable.getTableHeader().setReorderingAllowed(false);

		final CheckBoxHeader checkBoxHeader = new CheckBoxHeader(
				new CheckBoxItemListener());
		alertTable.getColumnModel().getColumn(0)
				.setHeaderRenderer(checkBoxHeader);

		frameGBC.gridx = 1;
		frameGBC.gridy = 1;
		frameGBC.weightx = 2;
		frameGBC.gridwidth = 3;
		add(alertScrollPane, frameGBC);

		popup = new JPopupMenu();
		deletePopupItem = new JMenuItem(DELETE_LABEL);
		duplicatePopupItem = new JMenuItem(DUPLICATE_LABEL);
		duplicatePopupItem.addActionListener(new DuplicateAlertListener());
		popup.add(deletePopupItem);
		popup.add(duplicatePopupItem);
		alertTable.addMouseListener(new PopupListener());

		// Result text area
		resultArea = new JTextArea(6, 20);
		resultScrollPane = new JScrollPane(resultArea);
		resultArea.setLineWrap(true);
		resultArea.setFont(new Font("Courier New", Font.PLAIN, 11));
		resultArea.setEditable(false);
		resultArea.setBorder(BorderFactory.createEtchedBorder());
		frameGBC.gridy = 2;
		frameGBC.gridx = 0;
		frameGBC.gridwidth = 5;
		frameGBC.ipady = 70;
		add(resultScrollPane, frameGBC);

		fileChooser = new JFileChooser();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		} catch (final InstantiationException e) {
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
		} catch (final UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		SwingUtilities.updateComponentTreeUI(this);
		SwingUtilities.updateComponentTreeUI(popup);
		SwingUtilities.updateComponentTreeUI(fileChooser);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentShown(final ComponentEvent e) {
				addSaveButton.grabFocus();
				alertTable.setRowSelectionInterval(0, 0);
			}
		});

		deletePopupItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				deleteSelectedAlert();
			}
		});

		alertTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(final KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_DELETE) {
					deleteSelectedAlert();
				}
			}
		});

		alertTable.getSelectionModel().addListSelectionListener(
				new AlertListSelectionListener());

		addSaveButton.addActionListener(new AddSaveButtonActionListener());
	}

	public void log(final String message) {
		resultArea.append(message);
		resultArea.setCaretPosition(resultArea.getCaretPosition()
				+ message.length());
	}

	public void openFile(final File file) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				alerts = provider.readAlertsFromFile(file);
				alertsToSendIndexes.clear();
				alertTable.tableChanged(new TableModelEvent(alertTable
						.getModel(), 1, alerts.size(),
						TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
			}
		});
	}

	public void restoreDefaults() {
		setAction(defaultAction);
		setAlertType(defaultAlertType);
		setSource(defaultSource);
		setSettings(defaultSettings);
		setPerceivedSeverity(defaultPerceivedSeverity);
		setSpecificReason(defaultSpecificReason);
	}

	public void setAction(final AlertAction action) {
		actions.clearSelection();
		final Enumeration<AbstractButton> buttons = actions.getElements();
		while (buttons.hasMoreElements()) {
			final AbstractButton button = buttons.nextElement();
			if (AlertAction.valueOf(button.getText()) == action) {
				actions.setSelected(button.getModel(), true);
				break;
			}
		}
	}

	public void setAgentKey(final String agentKey) {
		this.agentKey = agentKey;
	}

	public void setAlertType(final String alertType) {
		alertTypeField.setText(alertType);
	}

	public void setDefaultAction(final AlertAction defaultAction) {
		this.defaultAction = defaultAction;
	}

	public void setDefaultAlertType(final String defaultAlertType) {
		this.defaultAlertType = defaultAlertType;
	}

	public void setDefaultPerceivedSeverity(
			final PerceivedSeverity defaultPerceivedSeverity) {
		this.defaultPerceivedSeverity = defaultPerceivedSeverity;
	}

	public void setDefaultSettings(final String defaultSettings) {
		this.defaultSettings = defaultSettings;
	}

	public void setDefaultSource(final Source defaultSource) {
		this.defaultSource = defaultSource;
	}

	public void setDefaultSpecificReason(
			final SpecificReason defaultSpecificReason) {
		this.defaultSpecificReason = defaultSpecificReason;
	}

	public void setInput(final File file) {
		fileChooser.setCurrentDirectory(file);
		openFile(file);
	}

	public void setParameter(final String alertType) {
		parameterComboBox.setSelectedItem(alertType
				.substring(SharedModelConfiguration.IT09A_ALERT_TYPE_PREFIX
						.length()));
	}

	public void setPerceivedSeverity(final PerceivedSeverity perceivedSeverity) {
		perceivedSeverityComboBox.setSelectedItem(perceivedSeverity);
	}

	public void setSettings(final String settings) {
		settingsField.setText(settings);
	}

	public void setSource(final Source source) {
		sourceField.setText(source.getKey());
	}

	public void setSourceType(final Source.Type sourceType) {
		sourceTypeComboBox.setSelectedItem(sourceType);
	}

	public void setSpecificReason(final SpecificReason specificReason) {
		specificReasonComboBox.setSelectedItem(specificReason);
	}
}
