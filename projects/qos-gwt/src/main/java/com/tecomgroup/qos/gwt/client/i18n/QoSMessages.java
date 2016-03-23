/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.i18n;

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;
import com.google.gwt.i18n.client.Messages;
import com.google.gwt.user.client.ui.Image;
import com.tecomgroup.qos.messages.PolicyValidationMessages;

/**
 * @author abondin
 * 
 */
@DefaultLocale("en")
public interface QoSMessages
		extends
			Messages,
			CommonMessages,
			PolicyValidationMessages {
	@DefaultMessage("240p .flv")
	String label240pflv();

	@DefaultMessage("240p .mp4")
	String label240pmp4();

	@DefaultMessage("360p")
	String label360p();

	@DefaultMessage("Acknowledge")
	String acknowledge();

	@DefaultMessage("Acknowledged")
	String acknowledged();

	@DefaultMessage("Action")
	String action();

	@DefaultMessage("Add")
	String actionAdd();

	@Override
	@DefaultMessage("Cancel")
	String actionCancel();

	@DefaultMessage("Clear Filters")
	String actionClearFilters();

	@Override
	@DefaultMessage("Close")
	String actionClose();

	@DefaultMessage("Done")
	String actionDone();

	@Override
	@DefaultMessage("No")
	String actionNo();

	@Override
	@DefaultMessage("Ok")
	String actionOk();

	@DefaultMessage("Remove")
	String actionRemove();

	@DefaultMessage("Rename")
	String actionRename();

	@DefaultMessage("Save")
	String actionSave();

	@DefaultMessage("Select")
	String actionSelect();

	@Override
	@DefaultMessage("Yes")
	String actionYes();

	@DefaultMessage("Active")
	String active();

	@DefaultMessage("Add all probes")
	String addAllAgents();

	@DefaultMessage("All probes and tasks will be added to report. Confirm?")
	String addAllAgentsConfirm();

	@DefaultMessage("Add broadcasting")
	String addBroadcasting();

	@DefaultMessage("Added series")
	String addedChartSeries();

	@DefaultMessage("Additional user information")
	String additionalUserInformation();

	@DefaultMessage("Add notification")
	String addNotification();

	@DefaultMessage("Add recording")
	String addRecording();

	@DefaultMessage("Add series")
	String addSeries();

	@DefaultMessage("Add the widget to user dashboard?")
	String addWidgetToDashboardMessage();

	@DefaultMessage("Add the widget")
	String addWidgetToDashboardTitle();

	@DefaultMessage("After")
	String after();

	@DefaultMessage("Probe")
	String agent();

	@DefaultMessage("Unable to load probe")
	String agentLoadingFail();

	@DefaultMessage("Probe not selected")
	String agentNotSelected();

	@DefaultMessage("Cannot get agent policies count")
	String agentPoliciesCountFail();

	@DefaultMessage("Agent removal")
	String agentRemoval();

	@DefaultMessage("Agents have been successfully deleted")
	String agentsDeletedSuccessfully();

	@DefaultMessage("Unable to delete agents")
	String agentsDeletionFail();

	@DefaultMessage("Warning: all tasks, policies and alarms for selected agent(s) will be deleted. Besides, all templates and widgets associated with selected agent(s) will be updated/deleted.")
	String agentsDeletionLongDescription();

	@DefaultMessage("Probes List")
	String agentsList();

	@DefaultMessage("Unable to load probes")
	String agentsLoadingFail();

	@DefaultMessage("Are you sure you want to delete selected agents: {0}? ")
	String agentsMultipleDeletionQuestion(final String agents);

	@DefaultMessage("Agents removal")
	String agentsRemoval();

	@DefaultMessage("Are you sure you want to delete selected agent? ")
	String agentsSingleDeletionQuestion();

	@DefaultMessage("Alarm")
	String alert();

	@DefaultMessage("Unable to acknowledge alarm")
	String alertAcknowledgeFail();

	@DefaultMessage("Unable to clear alarm")
	String alertClearFail();

	@DefaultMessage("Unable to comment alarm")
	String alertCommentFail();

	@DefaultMessage("Alarm history")
	String alertHistory();

	@DefaultMessage("Alert notification mode has been successfully updated")
	String alertNotificationModeUpdated();

	@DefaultMessage("At least one probe must be selected.")
	String alertReportCriteriaCollectionIsEmpty();

	@DefaultMessage("Alarms")
	String alerts();

	@DefaultMessage("Unable to get alerts count")
	String alertsCountLoadingFail();

	@DefaultMessage("Unable to load alerts")
	String alertsLoadingFail();

	@DefaultMessage("Alarm templates")
	String alertsTemplates();

	@DefaultMessage("Alarms")
	String alertsViewTitle();

	@DefaultMessage("Template")
	String alertTemplateTitle();

	@DefaultMessage("Alarm type")
	String alertType();

	@DefaultMessage("Alarm type is not seleced")
	String alertTypeNotSelectedMessage();

	@DefaultMessage("Unable to load alarm types")
	String alertTypesLoadingFail();

	@DefaultMessage("Unable to unacknowledge alarm. Possible reason is lost connection to server. Please try to reload page.")
	String alertUnacknowledgeFail();

	@DefaultMessage("Acknowledged")
	String alertUpdateTypeAck();

	@DefaultMessage("Agent Restarted")
	String alertUpdateTypeAgentRestart();

	@DefaultMessage("Auto Cleared")
	String alertUpdateTypeAutoCleared();

	@DefaultMessage("Commented")
	String alertUpdateTypeComment();

	@DefaultMessage("New")
	String alertUpdateTypeNew();

	@DefaultMessage("Operator Cleared")
	String alertUpdateTypeOperatorCleared();

	@DefaultMessage("Operator Deleted")
	String alertUpdateTypeOperatorDeleted();

	@DefaultMessage("Repeated")
	String alertUpdateTypeRepeat();

	@DefaultMessage("Severity Degradation")
	String alertUpdateTypeSeverityDegradation();

	@DefaultMessage("Severity Upgrade")
	String alertUpdateTypeSeverityUpgrade();

	@DefaultMessage("Uncknowledged")
	String alertUpdateTypeUnack();

	@DefaultMessage("Updated")
	String alertUpdateTypeUpdate();

	@DefaultMessage("Chart type")
	String analyticsChartType();

	@DefaultMessage("Line")
	String chartTypeLine();

	@DefaultMessage("Area")
	String chartTypeArea();

	@DefaultMessage("Spline")
	String chartTypeSpline();

	@DefaultMessage("Column")
	String chartTypeColumn();

	@DefaultMessage("Arearange")
	String chartTypeArearange();

	@DefaultMessage("Step")
	String chartTypeStep();

	@DefaultMessage("QoS")
	String applicationName();

	@DefaultMessage("Application Version")
	String applicationVersion();

	@DefaultMessage("Apply")
	String apply();

	@DefaultMessage("Apply conditions template")
	String applyConditionsTemplate();

	@DefaultMessage("Apply notifications template")
	String applyNotificationTemplate();

	@DefaultMessage("Apply template")
	String applyTemplate();

	@DefaultMessage("April")
	String april();

	@DefaultMessage("Apr")
	String aprilShort();

	@DefaultMessage("Flickering")
	String audibleAlertModeMute();

	@DefaultMessage("Off")
	String audibleAlertModeOff();

	@DefaultMessage("Flickering and sound")
	String audibleAlertModeOn();

	@DefaultMessage("Alert notification mode")
	String audibleAlertNotificationMode();

	@DefaultMessage("August")
	String august();

	@DefaultMessage("Aug")
	String augustShort();

	@DefaultMessage("Autoscaling")
	String autoscaling();

	@DefaultMessage("Go back to Analysis page")
	String backToCharts();

	@DefaultMessage("Before")
	String before();

	@DefaultMessage("Boolean")
	String bool();

	@DefaultMessage("Broadcasting list")
	String broadcastList();

	@DefaultMessage("Build charts")
	String buildChart();

	@DefaultMessage("Build report")
	String buildReport();

	@DefaultMessage("Queued")
	String requestQueued();

	@DefaultMessage("Build Version")
	String buildVersion();

	@DefaultMessage("Cannot evaluate")
	String cannotEvalueateExpression();

	@DefaultMessage("Cannot load Alarm Details, please check request parameters")
	String cannotLoadAlertDetails();

	@DefaultMessage("Cease")
	@Override
	String cease();

	@DefaultMessage("Password change")
	String changePassword();

	@DefaultMessage("Chart")
	String chart();

	@DefaultMessage("Chart")
	String chartNamePrefix();

	@DefaultMessage("Chart not selected")
	String chartNotSelected();

	@DefaultMessage("Chart with the name {0} exists")
	String chartRenameFail(String chartName);

	@DefaultMessage("Please enter new name")
	String chartRenamePrompt();

	@DefaultMessage("All series from chart \"{0}\" are hidden")
	String chartSeriesAreHidden(String chartName);

	@DefaultMessage("Charts synchronization disabled")
	String chartsSynchronizationDisabled();

	@DefaultMessage("Charts synchronization enabled")
	String chartsSynchronizationEnabled();

	@DefaultMessage("Chart type")
	String chartType();

	@DefaultMessage("Check that all fields are valid")
	String checkFields();

	@DefaultMessage("Clear")
	String clear();

	@DefaultMessage("Clear charts")
	String clearCharts();

	@DefaultMessage("Are you sure that you want to delete selected series and charts?")
	String clearChartsConfirmation();

	@DefaultMessage("Cleared")
	String cleared();

	@DefaultMessage("Clear All Filters?")
	String clearFiltersConfirm();

	@DefaultMessage("Clear e-mail and sms notification actions")
	String clearPolicyActions();

	@DefaultMessage("All e-mail and sms notification actions of selected policies will be removed.")
	String clearPolicyActionsConfirmation();

	@DefaultMessage("Clear links between conditions templates and selected policies")
	String clearPolicyConditionsTemplates();

	@DefaultMessage("The links between conditions templates and selected policies will be removed.")
	String clearPolicyConditionsTemplatesConfirmation();

	@DefaultMessage("Clear template")
	String clearVideoSeriesTemplateName();

	@DefaultMessage("Clear series")
	String clearVideoSeriesDialogName();

	@DefaultMessage("Are you sure that you want to delete selected series?")
	String clearVideoSeriesConfirmation();

	@DefaultMessage("Codec")
	String codec();

	@Override
	@DefaultMessage("Comment")
	String comment();

	@DefaultMessage("Comments")
	String comments();

	@DefaultMessage("Conditions")
	String conditions();

	@DefaultMessage("Conditions editor")
	String conditionsEditor();

	@DefaultMessage("Conditions are not set")
	String conditionsNotSet();

	@DefaultMessage("Conditions template")
	String conditionsTemplate();

	@DefaultMessage("Conditions template editor")
	String conditionsTemplateEditor();

	@DefaultMessage("Confirmation")
	String confirmPassword();

	@DefaultMessage("Contact information")
	String contactInfo();

	@DefaultMessage("Copied to clipboard")
	String copiedToClipboard();

	@DefaultMessage("© 2013-2016 Q''ligent Inc. All rights reserved")
	String copyright();

	@DefaultMessage("Copy to clip")
	String copyToClipboard();

	@DefaultMessage("Count")
	String count();

	@DefaultMessage("Counter (>=0)")
	String counter();

	@DefaultMessage("Create")
	String create();

	@DefaultMessage("New user")
	String createNewAccount();

	@DefaultMessage("Add new group")
	String createNewGroup();

	@DefaultMessage("Add new group")
	String createNewGroupTitle();

	@DefaultMessage("Create new template")
	String createNewPolicyActionsTemplate();

	@DefaultMessage("Create policy")
	String createPolicyTitle();

	@DefaultMessage("Create widget")
	String createWidget();

	@DefaultMessage("Created at")
	String creationDateTime();

	@Override
	@DefaultMessage("Critical cease level must be less than or equal to warning cease level")
	String criticalCeaseLevelHigherThanWarningCeaseLevel();

	@Override
	@DefaultMessage("Critical cease level must be greater than or equal to warning cease level")
	String criticalCeaseLevelLessThanWarningCeaseLevel();

	@DefaultMessage("Critical level")
	String criticalLevel();

	@Override
	@DefaultMessage("Critical raise level must be less than or equal to critical cease level")
	String criticalRaiseLevelHigherThanCriticalCeaseLevel();

	@Override
	@DefaultMessage("Critical raise level must be less than or equal to warning raise level")
	String criticalRaiseLevelHigherThanWarningRaiseLevel();

	@Override
	@DefaultMessage("Critical raise level must be greater than or equal to critical cease level")
	String criticalRaiseLevelLessThanCriticalCeaseLevel();

	@Override
	@DefaultMessage("Critical raise level must be greater than or equal to warning raise level")
	String criticalRaiseLevelLessThanWarningRaiseLevel();

	@DefaultMessage("Current password")
	String currentPassword();

	@DefaultMessage("Current results")
	String currentResults();

	@DefaultMessage("Dashboard")
	String dashboard();

	@DefaultMessage("Unable to update dashboard")
	String dashboardUpdateFail();

	@DefaultMessage("Dashboard widgets limit is exceeded. Limit: {0} widgets")
	@AlternateMessage({"one",
			"Dashboard widgets limit is exceeded. Limit: {0} widget"})
	String dashboardWidgetLimitExceed(@PluralCount int limit);

	/**
	 * Provides multiple plural forms of word 'day' based on a count
	 * 
	 * @see Messages.PluralCount
	 * @param count
	 * @return
	 */
	@DefaultMessage("{0} days")
	@AlternateMessage({"one", "{0} day"})
	String days(@PluralCount int count);

	@DefaultMessage("December")
	String december();

	@DefaultMessage("Dec")
	String decemberShort();

	@DefaultMessage("Delete")
	String delete();

	@DefaultMessage("Some of recipients no longer exist. These recipients were cleared from the table.")
	String deletedRecipientsError();

	@DefaultMessage("Delete template")
	String deleteTemplate();

	@DefaultMessage("Delete event")
	String deleteEvent();

	@DefaultMessage("Are you sure that you want to delete template?")
	String deleteTemplateConfirmation();

	@DefaultMessage("Are you sure that you want to delete event?")
	String deleteEventConfirmation();

	@DefaultMessage("Delete selected")
	String deleteUserAccounts();

	@DefaultMessage("Are you sure that you want to delete selected users?")
	String deleteUserAccountsMessage();

	@DefaultMessage("Delete widget")
	String deleteWidget();

	@DefaultMessage("Are you sure that you want to delete widget?")
	String deleteWidgetConfirmation();

	@DefaultMessage("Destination")
	String destination();

	@DefaultMessage("Description")
	String description();

	@DefaultMessage("Details")
	String details();

	@DefaultMessage("Detection time value")
	String detectionTimeValue();

	@DefaultMessage("Device name")
	String deviceName();

	@DefaultMessage("Device number")
	String deviceNumber();

	@DefaultMessage("Device type")
	String deviceType();

	@DefaultMessage("Disable autoscaling")
	String disableAutoscaling();

	@DefaultMessage("Disable captions")
	String disableCaptions();

	@DefaultMessage("disabled")
	String disabled();

	@DefaultMessage("disabled")
	String disabledUser();

	@DefaultMessage("Disable value tooltip")
	String disableMouseTracking();

	@DefaultMessage("Disable thresholds")
	String disableThresholds();

	@DefaultMessage("Disable users")
	String disableUserAccounts();

	@DefaultMessage("Are you sure that you want to disable selected users?")
	String disableUserAccountsMessage();

	@DefaultMessage("Display Name")
	String displayName();

	@DefaultMessage("Downloading files")
	String downloadingFiles();

	@DefaultMessage("Download Video")
	String downloadVideo();

	@DefaultMessage("Error loading report")
	String downloadReportFail();

	@DefaultMessage("There are duplicate actions of the same type and with the same recipients.")
	String duplicateActionsError();

	@DefaultMessage("Duration")
	String duration();

	@DefaultMessage("Edit")
	String edit();

	@DefaultMessage("Edit group")
	String editGroupTitle();

	@DefaultMessage("Edit template")
	String editPolicyActionsTemplate();

	@DefaultMessage("Edit policy")
	String editPolicyTitle();

	@DefaultMessage("Edit profile")
	String editProfile();

	@DefaultMessage("E-mail")
	String email();

	@DefaultMessage("Select probe...")
	String emptyAgentText();

	@DefaultMessage("Enter filter text...")
	String emptyFilterText();

	@DefaultMessage("Select notification type...")
	String emptyNotificationType();

	@DefaultMessage("Select parameter...")
	String emptyParameterText();

	@DefaultMessage("There are no actions")
	String emptyPolicyActionsMessage();

	@DefaultMessage("Select recipient...")
	String emptyRecipientText();

	@DefaultMessage("Series list is empty")
	String emptySeries();

	@DefaultMessage("Select task...")
	String emptyTaskText();

	@DefaultMessage("Select template...")
	String emptyTemplateText();

	@DefaultMessage("Select user...")
	String emptyUserText();

	@DefaultMessage("Enable autoscaling")
	String enableAutoscaling();

	@DefaultMessage("Enable captions")
	String enableCaptions();

	@DefaultMessage("Enable value tooltip")
	String enableMouseTracking();

	@DefaultMessage("Enable thresholds")
	String enableThresholds();

	@DefaultMessage("Enable users")
	String enableUserAccounts();

	@DefaultMessage("Are you sure that you want to enable selected users?")
	String enableUserAccountsMessage();

	@DefaultMessage("Ended at")
	String endDateTime();

	@DefaultMessage("End of data")
	String endOfData();

	@DefaultMessage("English")
	String english();

	@DefaultMessage("Enter e-mail")
	String enterEmail();

	@DefaultMessage("Enter first name")
	String enterFirstName();

	@DefaultMessage("Enter last name")
	String enterLastName();

	@DefaultMessage("Enter login")
	String enterLogin();

	@DefaultMessage("Enter middle name")
	String enterMiddleName();

	@DefaultMessage("Enter phone")
	String enterPhone();

	@DefaultMessage("{0} with the same name already exists")
	String entityAlreadyExists(String entityName);

	@DefaultMessage("It is allowed to display only similar parameters with equal types and units in the same chart")
	String equalsMeasureUnitsOneChartConstraint();

	@Override
	@DefaultMessage("Error")
	String error();

	@DefaultMessage("Exclude from group")
	String excludeFromGroup();

	@DefaultMessage("Export")
	String export();

	@DefaultMessage("Aggregated data export")
	String exportAggregatedDataDialogTitle();

	@DefaultMessage("Unable to export results of task ({0})")
	String exportingResultsFail(long taskId);

	@DefaultMessage("Raw data export")
	String exportRawDataDialogTitle();

	@DefaultMessage("Please wait ...")
	String exportResultsLingeringTask();

	@DefaultMessage("Export to Excel")
	String exportToExcel();

	@DefaultMessage("February")
	String february();

	@DefaultMessage("Feb")
	String februaryShort();

	@DefaultMessage("This field is required")
	String fieldIsRequired();

	@DefaultMessage("Filters")
	String filters();

	@DefaultMessage("Finish")
	String finish();

	@DefaultMessage("First name")
	String firstName();

	@DefaultMessage("Chart widgets with fixed interval are not allowed")
	String fixedIntervalChartWidgetsNotAllowed();

	@DefaultMessage("FPS")
	String fps();

	@DefaultMessage("Friday")
	String friday();

	@DefaultMessage("Generate")
	String generate();

	@DefaultMessage("Agents")
	String gisAgents();

	@DefaultMessage("Agent \"{0}\" selected")
	String gisAgentSelected(String selectedAgent);

	@DefaultMessage("Agent statuses")
	String gisAgentStatuses();

	@DefaultMessage("Base Layer")
	String gisBaseLayer();

	@DefaultMessage("Map")
	String gisMap();

	@DefaultMessage("Overlays")
	String gisOverlays();

	@DefaultMessage("Group")
	String group();

	@DefaultMessage("Group has been successfully created")
	String groupCreatedSuccessfully();

	@DefaultMessage("Unable to create new group")
	String groupCreationFail();

	@DefaultMessage("Group has been deleted successfully")
	String groupDeletedSuccessfully();

	@DefaultMessage("Group deletion confirmation")
	String groupDeletionConfirmation();

	@DefaultMessage("Group \"{0}\" will be deleted. All policy actions with this group as a recipient will also be deleted.")
	String groupDeletionConfirmationMessage(String groupName);

	@DefaultMessage("Unable to remove group")
	String groupRemovalFail();

	@DefaultMessage("Group has been successfully saved")
	String groupSavedSuccessfully();

	@DefaultMessage("Unable to save the group")
	String groupSaveFail();

	@DefaultMessage("Group save error")
	String groupSaveFailTitle();

	@DefaultMessage("Unable to load groups")
	String groupsLoadingFail();

	@DefaultMessage("hour")
	String hoursShort();

	@DefaultMessage("Import from LDAP")
	String importFromLDAP();

	@DefaultMessage("Inactive")
	String inactive();

	@DefaultMessage("Incorrect current password")
	String incorrectCurrentPassword();

	@DefaultMessage("Phone number format is incorrect. Phone number must have a leading + symbol, and may contain digits, parentheses, white spaces and hyphens. Examples: +1 909 123 345, + 38 (123) 34-567-89")
	String incorrectPhoneNumberFormat();

	@DefaultMessage("Initializing...")
	String initializing();

	@DefaultMessage("{0} is not a valid date - it must be in the format {1} (for example {2})")
	String invalidDate(String date, String format, String example);

	@DefaultMessage("{0} is not a valid date - it must be in the format {1}")
	String invalidDateFormat(String date, String format);

	@DefaultMessage("Time interval is incorrect")
	String invalidDateTimeInterval();

	@DefaultMessage("{0} is not a valid time - it must be in the format {1} (for example {2})")
	String invalidTime(String time, String format, String example);

	@DefaultMessage("Unable to determinate agent time zone. Please check time zone spelling")
	String invalidTimeZone();

	@DefaultMessage("January")
	String january();

	@DefaultMessage("Jan")
	String januaryShort();

	@DefaultMessage("July")
	String july();

	@DefaultMessage("Jul")
	String julyShort();

	@DefaultMessage("June")
	String june();

	@DefaultMessage("Jun")
	String juneShort();

	@DefaultMessage("Key")
	String key();

	@DefaultMessage("Last name")
	String lastName();

	@DefaultMessage("Last updated at")
	String lastUpdateDateTime();

	@DefaultMessage("Legend")
	String legend();

	@Override
	@DefaultMessage("Level {0} is not completely defined")
	String levelIsNotCompletelyDefined(String level);

	@Override
	@DefaultMessage("Level {0} is not defined")
	String levelIsNotDefined(String level);

	@Override
	@DefaultMessage("Condition levels are not defined")
	String levelsAreNotDefined();

	@DefaultMessage("FTP")
	String linkFTP();

	@DefaultMessage("FTP (on Schedule)")
	String linkFTPonSchedule();

	@DefaultMessage("Link (probe storage)")
	String linkLocal();

	@DefaultMessage("Link to file")
	String linkToFile();

	@DefaultMessage("Live Video")
	String liveVideo();

	@DefaultMessage("Unable to load dashboard")
	String loadDashbordFail();

	@DefaultMessage("Loading...")
	String loading();

	@DefaultMessage("Loading aggregated...")
	String loadingAggregated();

	@DefaultMessage("Loading data...")
	String loadingData();

	@DefaultMessage("Cannot load data for widget ")
	String loadWidgetDataFail();

	@DefaultMessage("Login")
	String login();

	@DefaultMessage("Login failed: ")
	String loginFailed();

	@DefaultMessage("Login or password is incorrect")
	String loginIncorrectLogin();

	@DefaultMessage("Password")
	String loginPassword();

	@DefaultMessage("Remember Me")
	String loginRememberMe();

	@DefaultMessage("Login")
	String loginSubmit();

	@DefaultMessage("Login")
	String loginTitle();

	@DefaultMessage("The account was disabled. Contact your Administrator")
	String loginUserDisabled();

	@DefaultMessage("Username")
	String loginUsername();

	@DefaultMessage("Logout")
	String logout();

	@DefaultMessage("Logout error")
	String logoutError();

	@DefaultMessage("Are you sure you want to logout?")
	String logoutMessage();

	@DefaultMessage("March")
	String march();

	@DefaultMessage("Mar")
	String marchShort();

	@DefaultMessage("Fields marked with \'&nbsp{0}&nbsp\' are required for filling")
	String markerNotice(Image marker);

	@DefaultMessage("Video length must be less or equal to {0}")
	String maxVideoLengthExceeded(String maxLength);

	@DefaultMessage("May")
	String may();

	@DefaultMessage("May")
	String mayShort();

	@Override
	@DefaultMessage("Message")
	String message();

	@DefaultMessage("Middle name")
	String middleName();

	@DefaultMessage("Min time interval loaded: {0} minute(s)")
	String minTimeIntervalLoaded(String timeInMinutes);

	@DefaultMessage("min")
	String minutesShort();

	@DefaultMessage("Module name")
	String moduleName();

	@DefaultMessage("Monday")
	String monday();

	@DefaultMessage("Monitoring ({0})")
	String monitoring(String task);

	@DefaultMessage("Chart")
	String monitoringChart();

	@DefaultMessage("Configuration")
	String monitoringConfiguration();

	@DefaultMessage("Description")
	String monitoringDescription();

	@DefaultMessage("Device")
	String monitoringDevice();

	@DefaultMessage("End Date")
	String monitoringEndDate();

	@DefaultMessage("Start Date")
	String monitoringStartDate();

	@DefaultMessage("Table")
	String monitoringTable();

	@DefaultMessage("Time")
	String monitoringTimeAxis();

	@DefaultMessage("Value ({0})")
	String monitoringValue(String unit);

	@DefaultMessage("More...")
	String more();

	@Override
	@DefaultMessage("Name")
	String name();

	@DefaultMessage("File name")
	String fileName();

	@DefaultMessage("Agents List")
	String navigationAgentsList();

	@DefaultMessage("Alarms View")
	String navigationAlerts();

	@DefaultMessage("Analysis")
	String navigationAnalytics();

	@DefaultMessage("Dashboard")
	String navigationDashboard();

	@DefaultMessage("Probe Map")
	String navigationMap();

	@DefaultMessage("Probes and Tasks")
	String navigationProbesAndTasks();

	@DefaultMessage("System Information")
	String navigationSystemInfo();

	@DefaultMessage("Users")
	String navigationUsers();

	@DefaultMessage("Channel View")
	String navigationChannelView();

	@DefaultMessage("Probe Configuration")
	String navigationRemoteProbeConfig();

	@DefaultMessage("Recording")
	String navigationRecordSchedule();

	@DefaultMessage("Roles")
	String navigationRoles();

	@DefaultMessage("Net address")
	String netAddress();

	@DefaultMessage("New widget with the chart of the most faulty probes")
	String newAnalyticsChartWidget();

	@DefaultMessage("Create a new chart")
	String newChartRadio();

	@DefaultMessage("New map widget")
	String newMapWidget();

	@DefaultMessage("New password")
	String newPassword();

	@DefaultMessage("New value")
	String newValue();

	@DefaultMessage("New widget")
	String newWidget();

	@DefaultMessage("There are no probes to display")
	String noAgentsToDisplay();

	@DefaultMessage("There are no alarms")
	String noAlertsFound();

	@DefaultMessage("There is no data to display")
	String noAnalyticsData();

	@DefaultMessage("No data")
	String noData();

	@DefaultMessage("No data for task")
	String noParametersText();

	@DefaultMessage("There is no recorded video associated with alarm")
	String noRecordedStreamAssociatedWithAlert();

	@DefaultMessage("There is no results associated with alarm")
	String noResultsAssociatedWithAlert();

	@DefaultMessage("There is no results associated with alarm report")
	String noResultsAssociatedWithAlertReport();

	@DefaultMessage("There are no tasks with data")
	String noTasksText();

	@DefaultMessage("No templates")
	String noTemplatesText();

	@DefaultMessage("Notification language")
	String notificationLanguage();

	@DefaultMessage("Notification language has been successfully updated")
	String notificationLanguageUpdated();

	@DefaultMessage("Notifications")
	String notifications();

	@DefaultMessage("Notifications editor")
	String notificationsEditor();

	@DefaultMessage("Notifications template")
	String notificationsTemplate();

	@DefaultMessage("Notifications template editor")
	String notificationsTemplateEditor();

	@DefaultMessage("Not logged in")
	String notLoggedIn();

	@DefaultMessage("November")
	String november();

	@DefaultMessage("Nov")
	String novemberShort();

	@DefaultMessage("Numeric")
	String numeric();

	@DefaultMessage("October")
	String october();

	@DefaultMessage("Oct")
	String octoberShort();

	@DefaultMessage("On")
	String on();

	@DefaultMessage("Originator")
	String originator();

	@DefaultMessage("Originator Type")
	String originatorType();

	@DefaultMessage("Others")
	String others();

	@DefaultMessage("Requested page not found")
	String pageNotFound();

	@DefaultMessage("Next page")
	String pageTableNext();

	@DefaultMessage("Previous page")
	String pageTablePrevious();

	@DefaultMessage("Parameter")
	String parameter();

	@DefaultMessage("Parameter not selected")
	String parameterNotSelected();

	@DefaultMessage("Parameter type")
	String parameterType();

	@DefaultMessage("Policy parameter type ({0}) must be equal to template parameter type ({1}).")
	String parameterTypesAndConditionsTemplateContstraint(
			String policyParameterType, String templateParameterType);

	@DefaultMessage("It is allowed to apply conditions template only for policies with similar parameters with equal types and units.")
	String parameterTypesAndUnitsConditionsTemplateConstraint();

	@DefaultMessage("Password was successfully updated")
	String passwordUpdatedSuccessfully();

	@DefaultMessage("Unable to update password")
	String passwordUpdatingFail();

	@DefaultMessage("Severity")
	String perceivedSeverity();

	@DefaultMessage("Critical")
	String perceivedSeverityCritical();

	@DefaultMessage("Indeterminate")
	String perceivedSeverityIndeterminate();

	@DefaultMessage("Major")
	String perceivedSeverityMajor();

	@DefaultMessage("Minor")
	String perceivedSeverityMinor();

	@DefaultMessage("Notice")
	String perceivedSeverityNotice();

	@DefaultMessage("Warning")
	String perceivedSeverityWarning();

	@DefaultMessage("Percentage")
	String percentage();

	@DefaultMessage("Personal information")
	String personalInformation();

	@DefaultMessage("Phone number")
	String phone();

	@DefaultMessage("Platform")
	String platform();

	@DefaultMessage("Unable to download list of files")
	String playlistDownloadFail();

	@DefaultMessage("Policies")
	String policies();

	@DefaultMessage("Warning: Alarms for given policy will be cleared")
	String policiesDeleteMessage();

	@DefaultMessage("Unable to load policies")
	String policiesLoadingFail();

	@DefaultMessage("Unable to delete policies")
	String policiesRemovalFail();

	@DefaultMessage("Policy")
	String policy();

	@DefaultMessage("All actions of selected policies were successfully cleared.")
	String policyActionsClearedSuccessfully();

	@DefaultMessage("Policy actions template was successfully applied for selected policies.")
	String policyActionsTemplateAppliedSuccessfully();

	@DefaultMessage("Template has been successfully created")
	String policyComponentTemplateCreatedSuccessfully();

	@DefaultMessage("Unable to create template")
	String policyComponentTemplateCreationFail();

	@DefaultMessage("Templates deletion confirmation")
	String policyComponentTemplateDeletionConfirmation();

	@DefaultMessage("Selected templates will be deleted")
	String policyComponentTemplateDeletionConfirmationMessage();

	@DefaultMessage("Unable to delete templates")
	String policyComponentTemplateDeletionFail();

	@DefaultMessage("Would you like to apply changes of this template to the policies to which it was already applied before?")
	String policyComponentTemplateReapplyConfirm();

	@DefaultMessage("Templates have been successfully removed")
	String policyComponentTemplateRemovedSuccessfully();

	@DefaultMessage("Policy templates")
	String policyComponentTemplates();

	@DefaultMessage("Template has been successfully saved")
	String policyComponentTemplateSavedSuccessfully();

	@DefaultMessage("Unable to save template")
	String policyComponentTemplateSaveFail();

	@DefaultMessage("Policy conditions template was successfully applied for selected policies.")
	String policyConditionsTemplateAppliedSuccessfully();

	@DefaultMessage("The links between conditions templates and selected policies were successfully cleared.")
	String policyConditionsTemplatesClearedSuccessfully();

	@DefaultMessage("Parameter type has changed. The links between the template and the policies to which this template was already applied before will be cleared.")
	String policyConditionTemplateSaveTypeChangedConfirm();

	@DefaultMessage("Policy was successfully created")
	String policyCreatedSuccessfully();

	@DefaultMessage("Policy was successfully changed")
	String policyEditedSuccessfully();

	@DefaultMessage("Unable to load policy")
	String policyLoadingFail();

	@DefaultMessage("Policy with key {0} not found")
	String policyNotFound(String policyKey);

	@DefaultMessage("Notification type")
	String policyNotificationType();

	@DefaultMessage("Options")
	String policyOptions();

	@DefaultMessage("Previous value")
	String previousValue();

	@DefaultMessage("Primary user information")
	String primaryUserInformation();

	@DefaultMessage("Probable cause")
	String probableCause();

	@DefaultMessage("Threshold Crossed")
	String probableCauseThresholdCrossed();

	@DefaultMessage("Probe")
	String probe();

	@DefaultMessage("Probe")
	String probeShort();

	@DefaultMessage("Properties")
	String properties();

	@DefaultMessage("Property")
	String property();

	@DefaultMessage("Quality")
	String quality();

	@DefaultMessage("Raise")
	@Override
	String raise();

	@DefaultMessage("raw")
	String rawData();

	@DefaultMessage("Rebuild")
	String rebuild();

	@DefaultMessage("Report''s Criteria have been modified. Would you like to export the current results or rebuild the report?")
	String rebuildReportConfirm();

	@DefaultMessage("Recipients")
	String recipients();

	@DefaultMessage("Recipients for each action are required")
	String recipientsAreRequered();

	@DefaultMessage("From")
	String recordedVideoFrom();

	@DefaultMessage("To")
	String recordedVideoTo();

	@DefaultMessage("Recording list")
	String recordingList();

	@DefaultMessage("Report")
	String report();

	@DefaultMessage("Reports")
	String reports();

	@DefaultMessage("Report templates")
	String reportTemplates();

	@DefaultMessage("Unable to load results for this page")
	String resultLoadingFail();

	@DefaultMessage("Results")
	String results();

	@DefaultMessage("Downloading file with exported results...")
	String resultsFileDownloading();

	@DefaultMessage("Results retrieval: {0}% complete")
	String resultsRetrieval(int percentages);

	@DefaultMessage("Display the data for the past period")
	String resultsTableTimeWidgetHeader();

	@DefaultMessage("Result table")
	String resultTable();

	@DefaultMessage("Result templates")
	String resultTemplates();

	@DefaultMessage("Role")
	String role();

	@DefaultMessage("Administrator")
	String roleAdmin();

	@DefaultMessage("Roles")
	String roles();

	@DefaultMessage("User")
	String roleUser();

	@DefaultMessage("Configurator")
	String roleConfigurator();

	@DefaultMessage("SuperAdmin")
	String roleSuperAdmin();

	@DefaultMessage("Unable to get root system component")
	String rootSystemComponentLoadingFail();

	@DefaultMessage("You need to commit or cancel your changes")
	String rowEditorDirtyText();

	@DefaultMessage("Russian")
	String russian();

	@DefaultMessage("Interval, sec")
	String samplingRate();

	@DefaultMessage("Saturday")
	String saturday();

	@DefaultMessage("Save as image")
	String saveAsImage();

	@DefaultMessage("Schedule")
	String schedule();

	@DefaultMessage("Search")
	String search();

	@DefaultMessage("sec")
	String secondsShort();

	@DefaultMessage("Select probe")
	String selectAgents();

	@DefaultMessage("Select existing chart")
	String selectChartRadio();

	@DefaultMessage("Send an alarm")
	String sendAlert();

	@DefaultMessage("Send Alert")
	String sendAlertLabel();

	@DefaultMessage("Send an email")
	String sendEmail();

	@DefaultMessage("Send Email")
	String sendEmailLabel();

	@DefaultMessage("Send an sms")
	String sendSMS();

	@DefaultMessage("Send SMS")
	String sendSMSLabel();

	@DefaultMessage("September")
	String september();

	@DefaultMessage("Sep")
	String septemberShort();

	@DefaultMessage("Captions")
	String seriesCaptions();

	@DefaultMessage("This series exists in this chart")
	String seriesExistsConstraint();

	@DefaultMessage("Settings")
	String settings();

	@DefaultMessage("Unable to update user settings")
	String settingsUpdateFailure();

	@DefaultMessage("Severity changed at")
	String severityChangeDateTime();

	@DefaultMessage("Severity is not defined")
	String severityNotSelected();

	@DefaultMessage("show")
	String show();

	@DefaultMessage("Show data for selected period:")
	String showDataForSelectedPeriod();

	@DefaultMessage("Show legend")
	String showLegend();

	@DefaultMessage("Size")
	String size();

	@DefaultMessage("Skype")
	String skype();

	@DefaultMessage("Some alarms may not be shown because of applied filters")
	String someAlertsMayBeInvisible();

	@DefaultMessage("Source")
	String source();

	@DefaultMessage("Source type")
	String sourceType();

	@DefaultMessage("Agent")
	String sourceTypeAgent();

	@DefaultMessage("Module")
	String sourceTypeModule();

	@DefaultMessage("Policy")
	String sourceTypePolicy();

	@DefaultMessage("Policy Manager")
	String sourceTypePolicyManager();

	@DefaultMessage("Server")
	String sourceTypeServer();

	@DefaultMessage("Stream")
	String sourceTypeStream();

	@DefaultMessage("Task")
	String sourceTypeTask();

	@DefaultMessage("Specific reason")
	String specificReason();

	@DefaultMessage("None")
	String specificReasonNone();

	@DefaultMessage("Unknown")
	String specificReasonUnknown();

	@DefaultMessage("Start")
	String start();

	@DefaultMessage("Started at")
	String startDateTime();

	@DefaultMessage("Start of data")
	String startOfData();

	@DefaultMessage("Status")
	String status();

	@DefaultMessage("Cannot load streams")
	String streamLoadingFail();

	@DefaultMessage("Sunday")
	String sunday();

	@DefaultMessage("Synchronize view")
	String syncView();

	@DefaultMessage("Application")
	String sysInfoApplication();

	@DefaultMessage("Branch")
	String sysInfoBranch();

	@DefaultMessage("Build Information")
	String sysInfoBuildInfo();

	@DefaultMessage("Build Time")
	String sysInfoBuildTime();

	@DefaultMessage("Build User")
	String sysInfoBuildUser();

	@DefaultMessage("Commit Id")
	String sysInfoCommitId();

	@DefaultMessage("Commit Message")
	String sysInfoCommitMessage();

	@DefaultMessage("Commit Time")
	String sysInfoCommitTime();

	@DefaultMessage("Commit User")
	String sysInfoCommitUser();

	@DefaultMessage("System Information")
	String sysInfoTitle();

	@DefaultMessage("Last result time")
	String systemComponentLastResultTime();

	@DefaultMessage("Registration time")
	String systemComponentRegistrationTime();

	@DefaultMessage("aggregated")
	String tableData();

	@DefaultMessage("Task")
	String task();

	@DefaultMessage("Active")
	String taskActive();

	@DefaultMessage("Creation date")
	String taskCreationDate();

	@DefaultMessage("Unable to load task")
	String taskLoadingFail();

	@DefaultMessage("Are you sure you want to delete selected tasks: {0}? ")
	String taskMultipleDeletionQuestion(final String tasks);

	@DefaultMessage("Task not selected")
	String taskNotSelected();

	@DefaultMessage("Task number")
	String taskNumber();

	@DefaultMessage("Task removal")
	String taskRemoval();

	@DefaultMessage("Tasks have been deleted successfully")
	String tasksDeletedSuccessfully();

	@DefaultMessage("Unable to delete tasks")
	String tasksDeletionFail();

	@DefaultMessage("Warning: all policies and alarms for selected task(s) will be deleted. Besides, all templates and widgets associated with selected task(s) will be updated/deleted.")
	String tasksDeletionLongDescription();

	@DefaultMessage("Unable to load tasks for policies")
	String tasksForPoliciesLoadingFail();

	@DefaultMessage("Are you sure you want to delete selected task? ")
	String taskSingleDeletionQuestion();

	@DefaultMessage("Unable to load tasks")
	String tasksLoadingFail();

	@DefaultMessage("Tasks removal")
	String tasksRemoval();

	@DefaultMessage("Save template")
	String tempalteSavingHeader();

	@DefaultMessage("Template")
	String template();

	@DefaultMessage("Some part of template data was not loaded. It could be possibly removed from server during configuration update.")
	String templateDataLoadingFail();

	@DefaultMessage("Template is not valid. Please check template parameters before saving.")
	String templateIsNotValid();

	@DefaultMessage("Unable to load templates")
	String templateLoadingFail();

	@DefaultMessage("Load template")
	String templateLoadingHeader();

	@DefaultMessage("Template was successfully loaded")
	String templateLoadingSuccess();

	@DefaultMessage("Template must contain at least one action")
	String templateMustContainActions();

	@DefaultMessage("Template name")
	String templateName();

	@DefaultMessage("Unable to remove template")
	String templateRemovingFail();

	@DefaultMessage("Template was successfully removed")
	String templateRemovingSuccess();

	@DefaultMessage("Template with the same name is already exist. Rewrite?")
	String templateSavingConfirmation();

	@DefaultMessage("Unable to save template")
	String templateSavingFail();

	@DefaultMessage("Template was successfully saved")
	String templateSavingSuccess();

	@DefaultMessage("Thresholds")
	String thresholds();

	@DefaultMessage("Thresholds are not set. Default colors will be used.")
	String thresholdsAreNotSet();

	@DefaultMessage("Cease event if parameter")
	String thresholdTypeCeaseMessage();

	@DefaultMessage("Raise event if parameter")
	String thresholdTypeRaiseMessage();

	@DefaultMessage("Threshold value")
	String thresholdValue();

	@DefaultMessage("Thursday")
	String thursday();

	@DefaultMessage("Time")
	String time();

	@DefaultMessage("Probe time")
	String timeAgent();

	@DefaultMessage("Time Interval")
	String timeInterval();

	@DefaultMessage("Day")
	String timeIntervalDay();

	@DefaultMessage("15 minutes")
	String timeIntervalFifteenMinutes();

	@DefaultMessage("Hour")
	String timeIntervalHour();

	@DefaultMessage("Month")
	String timeIntervalMonth();

	@DefaultMessage("Time interval is not selected")
	String timeIntervalNotSelected();

	@DefaultMessage("Other")
	String timeIntervalOther();

	@DefaultMessage("Week")
	String timeIntervalWeek();

	@DefaultMessage("Local time")
	String timeLocal();

	@DefaultMessage("Timezone")
	String timezone();

	@DefaultMessage("Russian translit")
	String translit();

	@DefaultMessage("Tuesday")
	String tuesday();

	@DefaultMessage("Type")
	String type();

	@Override
	@DefaultMessage("Task {0} became deleted while creating policy. It is not allowed to create policy for deleted task.")
	String unableToCreatePolicyWithDeletedSource(String source);

	@Override
	@DefaultMessage("Task {0} became disabled while creating policy. It is not allowed to create policy for disabled task.")
	String unableToCreatePolicyWithDisabledSource(String source);

	@DefaultMessage("Unable to get map location")
	String unableToGetMapInfo();

	@Override
	@DefaultMessage("Unable to initialize condition levels")
	String unableToInitConditionLevels();

	@DefaultMessage("Unable to load recorded stream associated with alarm")
	String unableToLoadRecordedStreamAssociatedWithAlert();

	@DefaultMessage("Unable to save policy")
	String unableToSavePolicy();

	@Override
	@DefaultMessage("Task {0} became deleted while updating policy. It is not allowed to update policy for deleted task.")
	String unableToUpdatePolicyWithDeletedSource(String source);

	@Override
	@DefaultMessage("Task {0} became disabled while updating policy. It is not allowed to update policy for disabled task.")
	String unableToUpdatePolicyWithDisabledSource(String source);

	@DefaultMessage("Unacknowledge")
	String unAcknowledge();

	@DefaultMessage("Undo zoom")
	String undoZoom();

	@DefaultMessage("Unknown")
	String unknown();

	@DefaultMessage("It is impossible to display parameter with type {0}")
	String unsupportedParamTypeConstraint(String param);

	@DefaultMessage("Update")
	String update();

	@DefaultMessage("Updated")
	String updated();

	@DefaultMessage("User with the same name already exists")
	String userAlreadyExists();

	@DefaultMessage("User with the same name already exists and was deactivated by an Administrator.")
	String userAlreadyExistsAndDisabled();

	@DefaultMessage("User was successfully created")
	String userCreatedSuccessfully();

	@DefaultMessage("Selected user is already in the group")
	String userExistsInTheGroup();

	@DefaultMessage("User groups")
	String userGroups();

	@DefaultMessage("User information")
	String userInformation();

	@DefaultMessage("Active")
	String userIsActive();

	@DefaultMessage("Users")
	String users();

	@DefaultMessage("Unable to save user")
	String userSavingFail();

	@DefaultMessage("Unable to load users count")
	String usersCountLoadingFail();

	@DefaultMessage("Unable to disable users")
	String usersDisableFail();

	@DefaultMessage("Users have been successfully disabled")
	String usersDisableSuccess();

	@DefaultMessage("Unable to enable users")
	String usersEnableFail();

	@DefaultMessage("Users have been successfully enabled")
	String usersEnableSuccess();

	@DefaultMessage("User settings")
	String userSettings();

	@DefaultMessage("Download links")
	String downloadLinks();

	@DefaultMessage("System events")
	String probeEvents();

	@DefaultMessage("Unable to remove event")
	String probeEventRemoveFail();

	@DefaultMessage("Event has been successfully removed")
	String probeEventRemoveSuccess();

	@DefaultMessage("Unable to load users")
	String usersLoadingFail();

	@DefaultMessage("Unable to remove users")
	String usersRemovalFail();

	@DefaultMessage("Users have been successfully removed")
	String usersRemovalSuccess();

	@DefaultMessage("User templates")
	String userTemplates();

	@DefaultMessage("User information was successfully updated")
	String userUpdatedSuccessfully();

	@DefaultMessage("Value")
	String value();

	@DefaultMessage("Version")
	String version();

	@Override
	@DefaultMessage("Video")
	String video();

	@DefaultMessage("Video bitrate")
	String videobitrate();

	@DefaultMessage("Video is longer than default length. Only first {0} minutes will be shown.")
	String videoLengthIsMoreThanDefault(final long defaultVideoLengthInMin);

	@DefaultMessage("View data in table")
	String viewInTable();

	@DefaultMessage("Warning level")
	String warningLevel();

	@DefaultMessage("The request for transcoding and export has been submitted. Please use the link to download the proxy file.")
	String downloadVideoWarning();

	@Override
	@DefaultMessage("Warning raise level must be less than or equal to warning cease level")
	String warningRaiseLevelHigherThanWarningCeaseLevel();

	@Override
	@DefaultMessage("Warning raise level must be greater than or equal to warning cease level")
	String warningRaiseLevelLessThanWarningCeaseLevel();

	@DefaultMessage("Wednesday")
	String wednesday();

	@DefaultMessage("Widget has been successfully added to dashboard")
	String widgetAddedToDashboard();

	@DefaultMessage("Dashboard already contains the widget")
	String widgetAlreadyExists();

	@DefaultMessage("Dashboard does not contain the widget")
	String widgetDoesNotExist();

	@DefaultMessage("Widget has been successfully saved")
	String widgetSuccessfullySaved();

	@DefaultMessage("within")
	String within();

    @DefaultMessage("Pie chart")
    String pieChart();

    @DefaultMessage("Update every")
    String updateEvery();

	@DefaultMessage("Registration state")
	String registrationState();
	@DefaultMessage("Successful")
	String successfulState();
	@DefaultMessage("In progress")
	String inProgressState();
	@DefaultMessage("Partially")
	String partiallyState();
	@DefaultMessage("Failed")
	String failedState();
	@DefaultMessage("Waiting")
	String acceptedState();

	@DefaultMessage("Capacity")
	String capacity();

	@DefaultMessage("Export has been requested. Link to download will be available on your settings page")
	String videoExportSuccess();

	@DefaultMessage("Total bitrates")
	String total();

	@DefaultMessage("Mbit/s")
	String mBitS();

	@DefaultMessage("Task \"{0}\" is disabled")
	String disabledTask(String taskDisplayName);

	@DefaultMessage("Transcode waiting")
	String descriptionTranscodeWaiting();

	@DefaultMessage("Transcoding in progress")
	String descriptionTranscoding();

	@DefaultMessage("Waiting upload to remote file system")
	String descriptionUploadWaiting();

	@DefaultMessage("Uploading to remote file system")
	String descriptionUploading();

	@DefaultMessage("Missed records")
	String descriptionMissedRecords();

	@DefaultMessage("Error no disc space left")
	String descriptionNoFreeDiscSpace();

	@DefaultMessage("Transcoding fault")
	String descriptionTranscodingFault();

	@DefaultMessage("FTP transfer fault")
	String descriptionFtpFault();

	@DefaultMessage("Upload timeout expired")
	String descriptionUploadTimeoutExpired();

	@DefaultMessage("No records found")
	String descriptionMissingFiles();

	@DefaultMessage("Internal error")
	String descriptionInternalError();

	@DefaultMessage("Link expired")
	String descriptionExpired();

	@DefaultMessage("Failed")
	String actionFailed();

	@DefaultMessage("OK")
	String actionSuccess();
}
