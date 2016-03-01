/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.users;

import java.util.*;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.theme.base.client.panel.ContentPanelBaseAppearance;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HasLayout;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.BlurEvent;
import com.sencha.gxt.widget.core.client.event.BlurEvent.BlurHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.PasswordField;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.ValueBaseField;
import com.sencha.gxt.widget.core.client.form.validator.AbstractValidator;
import com.sencha.gxt.widget.core.client.form.validator.MaxLengthValidator;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.MUser.Role;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.users.UserInformationWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.users.UserLabelProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.StyleUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.RoleLabelProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.UserProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractEntityEditorDialogView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomComboBox;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator.EmailValidator;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator.FieldMatchValidator;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator.LoginValidator;
import com.tecomgroup.qos.util.SimpleUtils;


/**
 * @author meleshin.o
 * 
 */
public class UserInformationWidgetView
		extends
			AbstractEntityEditorDialogView<MUser, UserInformationWidgetPresenter>
		implements
			UserInformationWidgetPresenter.MyView {

	interface ViewUiBinder extends UiBinder<Widget, UserInformationWidgetView> {
	}

	private final static ViewUiBinder UI_BINDER = GWT
			.create(ViewUiBinder.class);

	private static final int CREATE_USER_DIALOG_HEIGHT = 300;

	private static final int EDIT_USER_DIALOG_HEIGHT = CREATE_USER_DIALOG_HEIGHT - 60;

	private static final int LDAP_CREATE_USER_DIALOG_HEIGHT = CREATE_USER_DIALOG_HEIGHT - 70;

	private static final int LDAP_EDIT_USER_DIALOG_HEIGHT = LDAP_CREATE_USER_DIALOG_HEIGHT - 50;

	private static final int RADIOBUTTON_WIDTH = 100;

	private static final int FIELD_WIDTH = 150;

	final Validator<String> phoneNumberValidator = new AbstractValidator<String>() {
		@Override
		public List<EditorError> validate(final Editor<String> editor,
				final String value) {
			List<EditorError> errors = null;

			if (SimpleUtils.isNotNullAndNotEmpty(value)
					&& !SimpleUtils.isPhoneNumberValid(value)) {
				final String message = messages.incorrectPhoneNumberFormat();
				errors = createError(editor, message, value);
			}
			return errors;
		}
	};

	@UiField(provided = true)
	protected ContentPanel optionsPanel;

	private ToggleGroup radioGroup;

	@UiField(provided = true)
	protected Radio createNewUserRadio;

	@UiField(provided = true)
	protected Radio selectFromLdapRadio;

	@UiField(provided = true)
	protected ContentPanel basicInformationPanel;

	@UiField(provided = true)
	protected ContentPanel secondaryInformationPanel;

	@UiField(provided = true)
	protected TabPanel tabPanel;

	private final Map<ValueBaseField<?>, Image> fieldMarkersMap = new HashMap<ValueBaseField<?>, Image>();

	@UiField
	protected TextField loginField;

	@UiField(provided = true)
	protected Image loginMarker;

	@UiField(provided = true)
	protected Image passwordMarker;

	@UiField(provided = true)
	protected PasswordField passwordField;

	private CustomComboBox<MUser> loginComboBox;

	@UiField(provided = true)
	protected FieldLabel loginFieldLabel;

	@UiField(provided = true)
	protected FieldLabel passwordFieldLabel;

	@UiField(provided = true)
	protected Image confirmPasswordMarker;

	@UiField(provided = true)
	protected FieldLabel confirmPasswordFieldLabel;

	@UiField(provided = true)
	protected FieldLabel roleFieldLabel;

	@UiField
	protected SimpleContainer passwordFieldContainer;

	@UiField
	protected SimpleContainer confirmPasswordFieldContainer;

	@UiField(provided = true)
	protected PasswordField confirmPasswordField;

	private CustomComboBox<MUser.Role> roleField;

	@UiField(provided = true)
	protected TextField firstNameField;

	@UiField(provided = true)
	protected FieldLabel firstNameFieldLabel;

	@UiField(provided = true)
	protected TextField middleNameField;

	@UiField(provided = true)
	protected FieldLabel middleNameFieldLabel;

	@UiField(provided = true)
	protected TextField lastNameField;

	@UiField(provided = true)
	protected FieldLabel lastNameFieldLabel;

	@UiField(provided = true)
	protected TextField emailField;

	@UiField(provided = true)
	protected FieldLabel emailFieldLabel;

	@UiField(provided = true)
	protected TextField phoneField;

	@UiField(provided = true)
	protected FieldLabel phoneFieldLabel;

	@UiField(provided = true)
	protected QoSDialog userInformationDialog;

	@UiField(provided = true)
	protected HTML notice;

	@UiField
	protected SimpleContainer noticeContainer;

	private MUser user;

	private boolean dialogLdapModeActive = false;

	private final String LDAP_ENABLED_PROPERTY = "client.security.ldap.enabled";

	private final boolean ldapEnabled;

	private final UserProperties properties = GWT.create(UserProperties.class);

	private final ListStore<MUser> ldapStore = new ListStore<MUser>(
			properties.key());

	@Inject
	public UserInformationWidgetView(final EventBus eventBus,
			final AppearanceFactory appearanceFactory,
			final QoSMessages messages) {
		super(eventBus, messages);

		ldapEnabled = Boolean.parseBoolean((String) AppUtils
				.getClientProperties().get(LDAP_ENABLED_PROPERTY));
	}

	@Override
	protected void actionOkButtonPressed() {
		final Map<String, MUser> users = getUsers();
		final String login = loginField.getValue();
		final MUser existingUser = users.get(login);
		final boolean editMode = isEditMode();

		if (editMode || existingUser == null) {
			if (validate()) {
				if (editMode) {
					updateUser(user);
				} else {
					saveUser(user);
				}
			} else {
				AppUtils.showErrorMessage(messages.checkFields());
			}
		} else {
			final String errorMessage;
			if (existingUser.isDisabled()) {
				errorMessage = messages.userAlreadyExistsAndDisabled();
			} else {
				errorMessage = messages.userAlreadyExists();
			}
			AppUtils.showErrorMessage(errorMessage);
		}
	}

	private void afterUiBinderInitialization() {
		bindFieldsAndMarkers();
		initializeValidators();
	}

	private void beforeUiBinderInitialization() {
		initializePanels();
		initializeFields();
		initializeRadioButtons();
	}

	private void bindFieldsAndMarkers() {
		fieldMarkersMap.put(loginField, loginMarker);
		fieldMarkersMap.put(loginComboBox, loginMarker);
		fieldMarkersMap.put(passwordField, passwordMarker);
		fieldMarkersMap.put(confirmPasswordField, confirmPasswordMarker);
	}

	private void clearFields() {
		setLoginField("");
		clearPasswords();
		setTextField(firstNameField, "");
		setTextField(middleNameField, "");
		setTextField(lastNameField, "");
		setTextField(emailField, "");
		setTextField(phoneField, "");
		loginComboBox.clear();
	}

	private void clearPasswords() {
		passwordField.clearInvalid();
		confirmPasswordField.clearInvalid();
		passwordField.setValue(null, false, true);
		confirmPasswordField.setValue(null, false, true);
	}

	private ContentPanel createContentPanel(
			final ContentPanelBaseAppearance appearance) {
		final ContentPanel panel = new ContentPanel(appearance);
		StyleUtils.configureNoHeaders(panel);

		return panel;
	}

	@Override
	protected QoSDialog createDialog() {
		userInformationDialog = super.createDialog();
		userInformationDialog.setDeferHeight(true);
		return userInformationDialog;
	}

	private FieldLabel createFieldLabel() {
		final FieldLabel fieldLabel = new FieldLabel();
		fieldLabel.addStyleName(appearanceFactory.resources().css()
				.textDisabledColor());
		return fieldLabel;
	}

	private CustomComboBox<MUser> createLoginComboBox() {
		final CustomComboBox<MUser> loginComboBox = new CustomComboBox<MUser>(
				ldapStore, new UserLabelProvider());
		loginComboBox.setWidth(150);
		loginComboBox.setUpdateValueOnSelection(true);
		loginComboBox.setAllowBlank(false);
		loginComboBox.setEmptyText(messages.emptyUserText());

		return loginComboBox;
	}

	private HTML createNotice() {
		final HTML notice = new HTML();

		notice.setHTML(messages.markerNotice(new Image(appearanceFactory
				.resources().sic())));
		notice.addStyleName(appearanceFactory.resources().css()
				.textDisabledColor());
		notice.addStyleName(appearanceFactory.resources().css().text11px());

		return notice;
	}

	private PasswordField createPasswordField() {
		final PasswordField field = new PasswordField();
		field.setWidth(FIELD_WIDTH);

		return field;
	}

	private CustomComboBox<MUser.Role> createRoleField() {
		final CustomComboBox<MUser.Role> roleField = new CustomComboBox<MUser.Role>(
				new ListStore<MUser.Role>(new ModelKeyProvider<MUser.Role>() {

					@Override
					public String getKey(final Role item) {
						return item.toString();
					}

				}), new RoleLabelProvider(messages),
				appearanceFactory.triggerFieldAppearance());
		roleField.getStore().addAll(Arrays.asList(MUser.Role.values()));
		roleField.setAllowBlank(false);
		roleField.setForceSelection(true);
		roleField.setTypeAhead(true);
		roleField.setTriggerAction(TriggerAction.ALL);
		roleField.setEditable(true);
		roleField.setUpdateValueOnSelection(true);
		roleField.setValue(MUser.Role.ROLE_USER, true, true);
		roleField.setWidth(FIELD_WIDTH);

		return roleField;
	}

	private TextField createTextField() {
		final TextField field = new TextField();
		field.setWidth(FIELD_WIDTH);
		return field;
	}

	private void disableDialogLdapMode() {
		radioGroup.setValue(createNewUserRadio);
		dialogLdapModeActive = false;
		loginFieldLabel.add(loginField);

		loginComboBox.removeFromParent();
		showPasswordFields();
		if (!isEditMode()) {
			setFieldRequired(loginComboBox, false);
			setFieldRequired(loginField, true);
			setFieldRequired(passwordField, true);
			setFieldRequired(confirmPasswordField, true);
		}

		tabPanel.setActiveWidget(basicInformationPanel);
		fitDialogHeight();
	}

	private void enableDialogLdapMode() {
		radioGroup.setValue(selectFromLdapRadio);
		dialogLdapModeActive = true;
		hidePasswordFields();
		setFieldRequired(loginField, false);
		setFieldRequired(passwordField, false);
		setFieldRequired(confirmPasswordField, false);

		if (isEditMode()) {
			loginFieldLabel.add(loginField);
			loginComboBox.removeFromParent();
		} else {
			loginField.removeFromParent();
			loginFieldLabel.add(loginComboBox);
			setFieldRequired(loginComboBox, true);
		}
		tabPanel.setActiveWidget(basicInformationPanel);
		fitDialogHeight();
	}

	private void fitDialogHeight() {
		int height = CREATE_USER_DIALOG_HEIGHT;
		final Widget activeTab = tabPanel.getActiveWidget();

		if (dialogLdapModeActive && (activeTab == basicInformationPanel)) {
			if (isEditMode()) {
				height = LDAP_EDIT_USER_DIALOG_HEIGHT;
			} else {
				height = LDAP_CREATE_USER_DIALOG_HEIGHT;
			}
		} else if (user != null && isEditMode()) {
			height = EDIT_USER_DIALOG_HEIGHT;
		} else if (activeTab == secondaryInformationPanel) {
			height = CREATE_USER_DIALOG_HEIGHT - 30;
		}

		dialog.setHeight(height);
	}

	@Override
	protected String getCreationDialogTitle() {
		return messages.createNewAccount();
	}

	@Override
	protected Widget getDialogContent() {
		// dialog content is placed by UiBinder
		return null;
	}

	@Override
	protected String getUpdateDialogTitle() {
		return messages.userInformation();
	}

	private List<Role> getUserRoles() {
		final Role roleValue = roleField.getValue();
		final List<Role> roles = new ArrayList<Role>();
		roles.add(roleValue);

		return roles;
	}

	private Map<String, MUser> getUsers() {
		return getUiHandlers().getUsers();
	}

	@Override
	public void hide() {
		super.hide();
		dialog.hide();
	}

	private void hidePasswordFields() {
		passwordFieldContainer.hide();
		confirmPasswordFieldContainer.hide();
		basicInformationPanel.forceLayout();
	}

	private void hideRadioButtons() {
		createNewUserRadio.hide();
		selectFromLdapRadio.hide();
		optionsPanel.forceLayout();
	}

	@Override
	@Inject
	public void initialize() {
		super.initialize();
		initializeUI();
		initializeListeners();
	}

	private void initializeFields() {
		final ImageResource markerImage = appearanceFactory.resources().sic();

		loginMarker = new Image(markerImage);
		loginField = createTextField();
		loginFieldLabel = createFieldLabel();
		loginComboBox = createLoginComboBox();

		passwordMarker = new Image(markerImage);
		passwordField = createPasswordField();
		passwordFieldLabel = createFieldLabel();

		confirmPasswordMarker = new Image(markerImage);
		confirmPasswordField = createPasswordField();
		confirmPasswordFieldLabel = createFieldLabel();

		roleField = createRoleField();
		roleFieldLabel = createFieldLabel();
		roleFieldLabel.setLabelWidth(ClientConstants.DEFAULT_FIELD_WIDTH);
		roleFieldLabel.add(roleField);

		firstNameField = createTextField();
		firstNameFieldLabel = createFieldLabel();

		middleNameField = createTextField();
		middleNameFieldLabel = createFieldLabel();

		lastNameField = createTextField();
		lastNameFieldLabel = createFieldLabel();

		emailField = createTextField();
		emailFieldLabel = createFieldLabel();

		phoneField = createTextField();
		phoneFieldLabel = createFieldLabel();
	}

	private void initializeListeners() {
		radioGroup
				.addValueChangeHandler(new ValueChangeHandler<HasValue<Boolean>>() {
					@Override
					public void onValueChange(
							final ValueChangeEvent<HasValue<Boolean>> event) {
						if (event.getValue() == createNewUserRadio) {
							disableDialogLdapMode();
						} else if (event.getValue() == selectFromLdapRadio) {
							enableDialogLdapMode();
						}
						clearFields();
					}
				});

		loginComboBox.addValueChangeHandler(new ValueChangeHandler<MUser>() {
			@Override
			public void onValueChange(final ValueChangeEvent<MUser> event) {
				setUser(event.getValue());
			}
		});
		passwordField.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(final BlurEvent event) {
				confirmPasswordField.validate();
			}
		});
	}

	private void initializePanels() {
		tabPanel = new TabPanel(appearanceFactory.tabPanelAppearance());
		tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {

			@Override
			public void onSelection(final SelectionEvent<Widget> event) {
				// FIXME: HACK: fix for tab panel layout bug (at rare
				// occasion elements overlap occurs)
				if (event.getSelectedItem() instanceof HasLayout) {
					((HasLayout) event.getSelectedItem()).forceLayout();
				}
				fitDialogHeight();
			}
		});
		// HOTFIX : Repaint bottom white strip in color of underlying panel
		tabPanel.addStyleName(appearanceFactory.resources().css()
				.userInformationTabPanel());
		optionsPanel = createContentPanel(appearanceFactory
				.framedPanelAppearance());
		basicInformationPanel = createContentPanel(appearanceFactory
				.lightFramedPanelAppearance());
		secondaryInformationPanel = createContentPanel(appearanceFactory
				.lightFramedPanelAppearance());

		notice = createNotice();
	}

	private void initializeRadioButtons() {
		radioGroup = new ToggleGroup();

		createNewUserRadio = new Radio();
		createNewUserRadio.setWidth(RADIOBUTTON_WIDTH);
		selectFromLdapRadio = new Radio();
		selectFromLdapRadio.setWidth(RADIOBUTTON_WIDTH);

		radioGroup.add(createNewUserRadio);
		radioGroup.add(selectFromLdapRadio);

		if (!ldapEnabled) {
			selectFromLdapRadio.disable();
		}
	}

	private void initializeUI() {
		beforeUiBinderInitialization();
		UI_BINDER.createAndBindUi(this);
		// IMPORTANT : specific operations such as validations or marker
		// bindings must complete after ui binder.
		afterUiBinderInitialization();
	}

	private void initializeValidators() {
		loginField.addValidator(new LoginValidator(messages));
		loginField.addValidator(new MaxLengthValidator(
				ClientConstants.USER_LOGIN_MAX_LENGTH));
		emailField.addValidator(new EmailValidator(messages));
		phoneField.addValidator(phoneNumberValidator);
		confirmPasswordField.addValidator(new FieldMatchValidator(
				passwordField, messages.confirmPassword(), messages
						.loginPassword(), messages));
	}

	private boolean isEditMode() {
		return user.getId() != null;
	}

	private boolean isLdapMode() {
		return dialogLdapModeActive
				|| (user != null && user.isLdapAuthenticated());
	}

	private boolean isLoginValid() {
		boolean result = true;
		if (isLdapMode()) {
			result = loginComboBox.isValid();
		} else {
			result = loginField.isValid();
		}

		return result;
	}

	private boolean isPasswordConfirmationValid() {
		boolean result = true;
		final boolean editMode = isEditMode();

		if ((editMode && SimpleUtils.isNotNullAndNotEmpty(passwordField
				.getValue())) || !editMode) {
			result = passwordField.isValid();
			result = confirmPasswordField.isValid();
		}

		return result;
	}

	@Override
	public void reset() {
		// do nothing, as the field values are set later based on MUser -
		// whether it's an existing entity, or an empty newly created stub
	}

	private void saveOrUpdateUser(final MUser user, final boolean updatePassword) {
		user.setFirstName(firstNameField.getValue());
		user.setSecondName(middleNameField.getValue());
		user.setLastName(lastNameField.getValue());
		user.setEmail(emailField.getValue());
		user.setPhone(phoneField.getValue());
		user.setRoles(getUserRoles());
		user.setLdapAuthenticated(dialogLdapModeActive);

		getUiHandlers().saveOrUpdateUser(user, updatePassword);
	}

	private void saveUser(final MUser user) {
		user.setLogin(loginField.getValue().trim());
		user.setPassword(passwordField.getValue());
		saveOrUpdateUser(user, true);
	}

	private void setFieldRequired(final ValueBaseField<?> field,
			final boolean required) {
		final Visibility visibility = required
				? Visibility.VISIBLE
				: Visibility.HIDDEN;
		final Widget marker = fieldMarkersMap.get(field);
		field.setAllowBlank(!required);
		marker.getElement().getStyle().setVisibility(visibility);
	}

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		if (tabPanel.getConfig(content.asWidget()) == null) {
			tabPanel.add(content.asWidget(), new TabItemConfig(slot.toString()));
		}
	}

	@Override
	public void setLdapUsers(final List<MUser> users) {
		ldapStore.clear();

		Collections.sort(users, new Comparator<MUser>() {

			@Override
			public int compare(final MUser user1, final MUser user2) {
				return user1.getLogin().compareToIgnoreCase(user2.getLogin());
			}
		});
		ldapStore.addAll(users);
	}

	private void setLoginField(final String login) {
		loginField.clearInvalid();
		setTextField(loginField, login);
		if (isEditMode()) {
			loginField.setReadOnly(true);
		} else {
			loginField.setReadOnly(false);
		}
	}

	private void assignRoleField(Role role) {
		if (role != Role.ROLE_SUPER_ADMIN) {
			roleField.getStore().remove(Role.ROLE_SUPER_ADMIN);
		}
		roleField.clearInvalid();
		roleField.setValue(role);
	}

	private void setRoleField(final MUser user) {

		if(user.getLogin() == null) {
			assignRoleField(Role.ROLE_USER);
			return;
		} else {
			for (MUser.Role role : MUser.Role.values()) {
				if (user.hasRole(role)) {
					assignRoleField(role);
					return;
				}
			}
		}
	}

	private void setTextField(final TextField field, final String value) {
		if (SimpleUtils.isNotNullAndNotEmpty(value)) {
			field.clearInvalid();
			field.setValue(value);
		} else {
			field.clear();
		}
	}

	@Override
	public void setUser(final MUser user) {
		if (user != null) {
			this.user = user;
			updateForm(user);
		}
	}

	@Override
	public void show() {
		tabPanel.setActiveWidget(tabPanel.getWidget(0));
		dialog.show();
	}

	private void showPasswordFields() {
		passwordFieldContainer.show();
		confirmPasswordFieldContainer.show();
		basicInformationPanel.forceLayout();
	}

	private void showRadioButtons() {
		createNewUserRadio.show();
		selectFromLdapRadio.show();
		optionsPanel.forceLayout();
	}

	private void toggleEditMode() {
		String saveButtonText = messages.create();
		String dialogHeadingText = messages.createNewAccount();

		if (isEditMode()) {
			saveButtonText = messages.actionSave();
			dialogHeadingText = messages.userInformation();
			hideRadioButtons();
			setFieldRequired(loginComboBox, false);
			setFieldRequired(loginField, false);
			setFieldRequired(passwordField, false);
			setFieldRequired(confirmPasswordField, false);
			noticeContainer.hide();
		} else {
			showRadioButtons();
			noticeContainer.show();
		}

		dialog.setHeadingText(dialogHeadingText);
		final TextButton button = (TextButton) dialog.getButtonBar()
				.getItemByItemId(PredefinedButton.OK.name());
		button.setText(saveButtonText);
	}

	private void toggleLdapMode() {
		if (user.isLdapAuthenticated()) {
			enableDialogLdapMode();
		} else {
			disableDialogLdapMode();
		}
	}

	private void updateForm(final MUser user) {
		fitDialogHeight();
		// IMPORTANT : Do not reorder.
		toggleEditMode();
		// IMPORTANT : Do not reorder.
		toggleLdapMode();

		// For correct resizing of tab panel after resizing radio buttons panel
		dialog.forceLayout();

		setLoginField(user.getLogin());
		setRoleField(user);
		setTextField(firstNameField, user.getFirstName());
		setTextField(middleNameField, user.getSecondName());
		setTextField(lastNameField, user.getLastName());
		setTextField(emailField, user.getEmail());
		setTextField(phoneField, user.getPhone());
		clearPasswords();

	}

	private void updateUser(final MUser user) {
		boolean updatePassword = false;
		if (SimpleUtils.isNotNullAndNotEmpty(passwordField.getValue())) {
			user.setPassword(passwordField.getValue());
			updatePassword = true;
		}
		saveOrUpdateUser(user, updatePassword);
	}

	@Override
	public boolean validate() {
		boolean result = true;

		if (!isLoginValid()) {
			result = false;
		}

		if (!isLdapMode() && !isPasswordConfirmationValid()) {
			result = false;
		}

		if (!roleField.isValid()) {
			result = false;
		}

		if (!firstNameField.isValid() || !middleNameField.isValid()
				|| !lastNameField.isValid() || !emailField.isValid()
				|| !phoneField.isValid()) {
			result = false;
		}

		return result;
	}
}
