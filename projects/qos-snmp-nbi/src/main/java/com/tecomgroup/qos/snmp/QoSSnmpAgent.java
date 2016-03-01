/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.snmp;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.tecomgroup.qos.service.SnmpService;
import org.apache.log4j.Logger;
import org.snmp4j.TransportMapping;
import org.snmp4j.agent.BaseAgent;
import org.snmp4j.agent.CommandProcessor;
import org.snmp4j.agent.DuplicateRegistrationException;
import org.snmp4j.agent.mo.DefaultMOMutableRow2PC;
import org.snmp4j.agent.mo.DefaultMOTable;
import org.snmp4j.agent.mo.MOAccessImpl;
import org.snmp4j.agent.mo.MOColumn;
import org.snmp4j.agent.mo.MOFactory;
import org.snmp4j.agent.mo.MOMutableColumn;
import org.snmp4j.agent.mo.MOMutableTableModel;
import org.snmp4j.agent.mo.MOTableIndex;
import org.snmp4j.agent.mo.MOTableRow;
import org.snmp4j.agent.mo.MOTableSubIndex;
import org.snmp4j.agent.mo.snmp.NotificationOriginatorImpl;
import org.snmp4j.agent.mo.snmp.RowStatus;
import org.snmp4j.agent.mo.snmp.SnmpCommunityMIB;
import org.snmp4j.agent.mo.snmp.SnmpNotificationMIB;
import org.snmp4j.agent.mo.snmp.SnmpTargetMIB;
import org.snmp4j.agent.mo.snmp.StorageType;
import org.snmp4j.agent.mo.snmp.TransportDomains;
import org.snmp4j.agent.mo.snmp.VacmMIB;
import org.snmp4j.agent.security.MutableVACM;
import org.snmp4j.log.Log4jLogFactory;
import org.snmp4j.log.LogFactory;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.MessageProcessingModel;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModel;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Gauge32;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.SMIConstants;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.MAlertUpdate;
import com.tecomgroup.qos.event.AbstractEvent;
import com.tecomgroup.qos.event.AlertUpdateEvent;
import com.tecomgroup.qos.event.QoSEventFilter;
import com.tecomgroup.qos.event.QoSEventListener;
import com.tecomgroup.qos.service.InternalEventBroadcaster;
import com.tecomgroup.qos.snmp.mib.EntityMib;
import com.tecomgroup.qos.snmp.mib.QligentVisionMib;
import com.tecomgroup.qos.snmp.util.EntityConverter;

/**
 * QoS SNMP Agent's base class.
 * 
 * @author novohatskiy.r
 * 
 */
public class QoSSnmpAgent extends BaseAgent implements DisposableBean {

	static {
		LogFactory.setLogFactory(new Log4jLogFactory());
	}

	private final static Logger LOGGER = Logger.getLogger(QoSSnmpAgent.class);

	@Autowired
	private SnmpService snmpService;

	@Value("${snmp.nbi.port}")
	private Integer snmpPort;

	@Value("${snmp.nbi.community.string}")
	private String communityString;
	@Value("${snmp.nbi.community.name}")
	private String communityName;
	@Value("${snmp.nbi.community.security.name}")
	private String communitySecurityName;
	@Value("${snmp.nbi.community.context.name}")
	private String communityContextName;

	// http://www.iana.org/assignments/enterprise-numbers
	private final static OID SYS_OID = new OID("1.3.6.1.4.1.40554.2.0");
	@Value("${snmp.nbi.sys.contact}")
	private String sysContact;
	@Value("${snmp.nbi.sys.name}")
	private String sysName;
	@Value("${snmp.nbi.sys.descr}")
	private String sysDescr;
	@Value("${snmp.nbi.sys.location}")
	private String sysLocation;
	@Value("${snmp.nbi.sys.services}")
	private Integer sysServices;

	@Value("#{T(com.tecomgroup.qos.util.Utils).parseListOfMaps(${snmp.nbi.accesses})}")
	private List<Map<String, String>> accesses;
	@Value("#{T(com.tecomgroup.qos.util.Utils).parseListOfMaps(${snmp.nbi.groups})}")
	private List<Map<String, String>> groups;
	@Value("#{T(com.tecomgroup.qos.util.Utils).parseListOfMaps(${snmp.nbi.users})}")
	private List<Map<String, String>> users;
	@Value("#{T(com.tecomgroup.qos.util.Utils).parseListOfMaps(${snmp.nbi.trap.receivers})}")
	private List<Map<String, String>> trapReceivers;
	@Value("#{T(com.tecomgroup.qos.util.Utils).parseListOfMaps(${snmp.nbi.trap.parameters})}")
	private List<Map<String, String>> trapParameters;

	@Resource(name = "QoSMOFactory")
	private MOFactory moFactory;
	@Autowired
	private InternalEventBroadcaster internalEventBroadcaster;

	private EntityMib entityMib;
	private QligentVisionMib visionMib;

	public QoSSnmpAgent(final String bootCounterFilename,
			final String bootConfigFilename) {
		super(new File(bootCounterFilename), new File(bootConfigFilename),
				new CommandProcessor(
						new OctetString(MPv3.createLocalEngineID())));
	}

	/**
	 * Adds community to security name mappings needed for SNMPv1 and SNMPv2c.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	protected void addCommunities(final SnmpCommunityMIB communityMIB) {
		checkSecurityNameDefined(communitySecurityName);

		final Variable[] vars = new Variable[]{new OctetString(communityName),
				new OctetString(communitySecurityName),
				getAgent().getContextEngineID(),
				new OctetString(communityContextName), new OctetString(),
				new Integer32(StorageType.nonVolatile),
				new Integer32(RowStatus.active)};

		final MOTableRow row = communityMIB.getSnmpCommunityEntry().createRow(
				new OctetString(communityString).toSubIndex(true), vars);
		communityMIB.getSnmpCommunityEntry().addRow(row);

	}

	@Override
	protected void addNotificationTargets(final SnmpTargetMIB targetMIB,
			final SnmpNotificationMIB notificationMIB) {
		targetMIB.addDefaultTDomains();

		notificationMIB.addNotifyEntry(new OctetString(SnmpConstants.TRAP),
				new OctetString(SnmpConstants.TRAP),
				SnmpNotificationMIB.SnmpNotifyTypeEnum.trap,
				StorageType.permanent);
		notificationMIB.addNotifyEntry(new OctetString(SnmpConstants.INFORM),
				new OctetString(SnmpConstants.INFORM),
				SnmpNotificationMIB.SnmpNotifyTypeEnum.inform,
				StorageType.permanent);

		for (final Map<String, String> receiver : trapReceivers) {
			final String parametersName = receiver
					.get(SnmpConstants.TRAP_RECEIVER_PARAMETERS);
			checkTrapParametersDefined(parametersName);

			final OctetString name = new OctetString(
					receiver.get(SnmpConstants.TRAP_RECEIVER_NAME));
			final OID transportDomain = getTransportDomain(receiver
					.get(SnmpConstants.TRAP_RECEIVER_TDOMAIN));
			final OctetString address = new OctetString(
					new UdpAddress(receiver
							.get(SnmpConstants.TRAP_RECEIVER_TADDRESS))
							.getValue());
			final int timeout = Integer.parseInt(receiver
					.get(SnmpConstants.TRAP_RECEIVER_TIMEOUT));
			final int retryCount = Integer.parseInt(receiver
					.get(SnmpConstants.TRAP_RECEIVER_RETRY_COUNT));
			final OctetString tagList = getTagList(receiver
					.get(SnmpConstants.TRAP_RECEIVER_TAG));

			targetMIB.addTargetAddress(name, transportDomain, address, timeout,
					retryCount, tagList, new OctetString(parametersName),
					StorageType.permanent);
		}

		for (final Map<String, String> currentTrapParameters : trapParameters) {
			final String securityName = currentTrapParameters
					.get(SnmpConstants.TRAP_PARAMETERS_SECURITY_NAME);
			checkSecurityNameDefined(securityName);

			final OctetString name = new OctetString(
					currentTrapParameters
							.get(SnmpConstants.TRAP_PARAMETERS_NAME));
			final int mpModel = getMpModel(currentTrapParameters
					.get(SnmpConstants.TRAP_PARAMETERS_MPMODEL));
			final int securityModel = getSecurityModel(currentTrapParameters
					.get(SnmpConstants.TRAP_PARAMETERS_SECURITY_MODEL));
			final int securityLevel = getSecurityLevel(currentTrapParameters
					.get(SnmpConstants.TRAP_PARAMETERS_SECURITY_LEVEL));

			targetMIB.addTargetParams(name, mpModel, securityModel,
					new OctetString(securityName), securityLevel,
					StorageType.permanent);
		}
	}

	@Override
	protected void addUsmUser(final USM usm) {
		for (final Map<String, String> userMap : users) {
			final OctetString securityName = new OctetString(
					userMap.get(SnmpConstants.USER_SECURITY_NAME));
			final int securityLevel = getSecurityLevel(userMap
					.get(SnmpConstants.USER_SECURITY_LEVEL));

			OID authProtocol = null;
			OctetString authPass = null;
			OID privProtocol = null;
			OctetString privPass = null;
			switch (securityLevel) {
				case SecurityLevel.NOAUTH_NOPRIV :
					break;
				case SecurityLevel.AUTH_PRIV :
					privProtocol = getPrivProtocolOID(userMap
							.get(SnmpConstants.USER_PRIV_PROTOCOL));
					privPass = new OctetString(
							userMap.get(SnmpConstants.USER_PRIV_PASS));
				case SecurityLevel.AUTH_NOPRIV :
					authProtocol = getAuthProtocolOID(userMap
							.get(SnmpConstants.USER_AUTH_PROTOCOL));
					authPass = new OctetString(
							userMap.get(SnmpConstants.USER_AUTH_PASS));
					break;
			}

			final UsmUser user = new UsmUser(securityName, authProtocol,
					authPass, privProtocol, privPass);
			usm.addUser(user.getSecurityName(), user);
		}
	}

	/**
	 * Adds initial VACM configuration.
	 */
	@Override
	protected void addViews(final VacmMIB vacmMIB) {
		for (final Map<String, String> groupMap : groups) {
			final String securityName = groupMap
					.get(SnmpConstants.GROUP_SECURITY_NAME);
			checkSecurityNameDefined(securityName);

			final int securityModel = getSecurityModel(groupMap
					.get(SnmpConstants.GROUP_SECURITY_MODEL));
			final OctetString groupName = new OctetString(
					groupMap.get(SnmpConstants.GROUP_NAME));

			vacmMIB.addGroup(securityModel, new OctetString(securityName),
					groupName, StorageType.nonVolatile);
		}

		for (final Map<String, String> accessMap : accesses) {
			final OctetString groupName = new OctetString(
					accessMap.get(SnmpConstants.ACCESS_GROUP_NAME));
			final OctetString contextPrefix = new OctetString(
					accessMap.get(SnmpConstants.ACCESS_CONTEXT_PREFIX));
			final int securityModel = getSecurityModel(accessMap
					.get(SnmpConstants.ACCESS_SECURITY_MODEL));
			final int securityLevel = getSecurityLevel(accessMap
					.get(SnmpConstants.ACCESS_SECURITY_LEVEL));

			vacmMIB.addAccess(groupName, contextPrefix, securityModel,
					securityLevel, MutableVACM.VACM_MATCH_EXACT,
					new OctetString(SnmpConstants.VACM_FULL_READ_VIEW),
					new OctetString(SnmpConstants.VACM_DISALLOWED_WRITE_VIEW),
					new OctetString(SnmpConstants.VACM_FULL_NOTIFY_VIEW),
					StorageType.nonVolatile);
		}

		vacmMIB.addViewTreeFamily(new OctetString(
				SnmpConstants.VACM_FULL_READ_VIEW), new OID(
				SnmpConstants.VACM_OID_SUBTREE), new OctetString(),
				VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
		vacmMIB.addViewTreeFamily(new OctetString(
				SnmpConstants.VACM_DISALLOWED_WRITE_VIEW), new OID(
				SnmpConstants.VACM_OID_SUBTREE), new OctetString(),
				VacmMIB.vacmViewExcluded, StorageType.nonVolatile);
		vacmMIB.addViewTreeFamily(new OctetString(
				SnmpConstants.VACM_FULL_NOTIFY_VIEW), new OID(
				SnmpConstants.VACM_OID_SUBTREE), new OctetString(),
				VacmMIB.vacmViewIncluded, StorageType.nonVolatile);
	}

	private void checkSecurityNameDefined(final String securityName) {
		boolean defined = false;
		for (final Map<String, String> userMap : users) {
			if (securityName.equals(userMap
					.get(SnmpConstants.USER_SECURITY_NAME))) {
				defined = true;
				break;
			}
		}

		if (!defined) {
			throw new IllegalArgumentException("Security name " + securityName
					+ " not defined in property "
					+ SnmpConstants.USER_SECURITY_NAME);
		}

	}

	private void checkTrapParametersDefined(final String parametersName) {
		boolean defined = false;
		for (final Map<String, String> currentTrapParameters : trapParameters) {
			if (parametersName.equals(currentTrapParameters
					.get(SnmpConstants.TRAP_PARAMETERS_NAME))) {
				defined = true;
				break;
			}
		}
		if (!defined) {
			throw new IllegalArgumentException("Params name " + parametersName
					+ " not defined in property "
					+ SnmpConstants.TRAP_PARAMETERS_NAME);
		}
	}

	/**
	 * Static stub for SNMP MIB-II ifTable.
	 * 
	 * @return
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	private DefaultMOTable createStaticIfTable() {
		final MOTableSubIndex[] subIndexes = new MOTableSubIndex[]{new MOTableSubIndex(
				SMIConstants.SYNTAX_INTEGER)};
		final MOTableIndex indexDef = new MOTableIndex(subIndexes, false);
		final MOColumn[] columns = new MOColumn[8];
		int c = 0;
		columns[c++] = new MOColumn(c, SMIConstants.SYNTAX_INTEGER,
				MOAccessImpl.ACCESS_READ_ONLY); // ifIndex
		columns[c++] = new MOColumn(c, SMIConstants.SYNTAX_OCTET_STRING,
				MOAccessImpl.ACCESS_READ_ONLY);// ifDescr
		columns[c++] = new MOColumn(c, SMIConstants.SYNTAX_INTEGER,
				MOAccessImpl.ACCESS_READ_ONLY); // ifType
		columns[c++] = new MOColumn(c, SMIConstants.SYNTAX_INTEGER,
				MOAccessImpl.ACCESS_READ_ONLY); // ifMtu
		columns[c++] = new MOColumn(c, SMIConstants.SYNTAX_GAUGE32,
				MOAccessImpl.ACCESS_READ_ONLY); // ifSpeed
		columns[c++] = new MOColumn(c, SMIConstants.SYNTAX_OCTET_STRING,
				MOAccessImpl.ACCESS_READ_ONLY);// ifPhysAddress
		columns[c++] = new MOMutableColumn(c, SMIConstants.SYNTAX_INTEGER, // ifAdminStatus
				MOAccessImpl.ACCESS_READ_WRITE, null);
		columns[c++] = new MOColumn(c, SMIConstants.SYNTAX_INTEGER,
				MOAccessImpl.ACCESS_READ_ONLY); // ifOperStatus

		final DefaultMOTable ifTable = new DefaultMOTable(new OID(
				"1.3.6.1.2.1.2.2.1"), indexDef, columns);
		final MOMutableTableModel model = (MOMutableTableModel) ifTable
				.getModel();
		final Variable[] rowValues1 = new Variable[]{new Integer32(1),
				new OctetString("Q'ligent Server"), new Integer32(1),
				new Integer32(0), new Gauge32(100000000), new OctetString(""),
				new Integer32(1), new Integer32(1)};
		model.addRow(new DefaultMOMutableRow2PC(new OID("1"), rowValues1));
		ifTable.setVolatile(true);
		return ifTable;
	}

	@Override
	public void destroy() throws Exception {
		saveConfig();
		session.close();
	}

	private OID getAuthProtocolOID(final String property) {
		OID oid = null;
		if ("MD5".equals(property)) {
			oid = AuthMD5.ID;
		} else if ("SHA".equals(property)) {
			oid = AuthSHA.ID;
		} else {
			throw new IllegalArgumentException("Wrong value " + property
					+ " for property " + SnmpConstants.USER_AUTH_PROTOCOL
					+ ". Only following options are allowed: MD5, SHA.");
		}

		return oid;
	}

	private int getMpModel(final String property) {
		int mpModel = -1;
		if ("MPv1".equals(property)) {
			mpModel = MessageProcessingModel.MPv1;
		} else if ("MPv2c".equals(property)) {
			mpModel = MessageProcessingModel.MPv2c;
		} else if ("MPv2u".equals(property)) {
			mpModel = MessageProcessingModel.MPv2u;
		} else if ("MPv3".equals(property)) {
			mpModel = MessageProcessingModel.MPv3;
		} else {
			throw new IllegalArgumentException("Wrong value " + property
					+ " for message processing model property."
					+ ". Only following options are allowed:"
					+ " MPV1, MPv2c, MPv2u, MPv3.");
		}
		return mpModel;
	}

	private OID getPrivProtocolOID(final String property) {
		OID oid = null;
		if ("DES".equals(property)) {
			oid = PrivDES.ID;
		} else if ("3DES".equals(property)) {
			oid = Priv3DES.ID;
		} else if ("AES128".equals(property)) {
			oid = PrivAES128.ID;
		} else if ("AES192".equals(property)) {
			oid = PrivAES192.ID;
		} else if ("AES256".equals(property)) {
			oid = PrivAES256.ID;
		} else {
			throw new IllegalArgumentException("Wrong value " + property
					+ " for property " + SnmpConstants.USER_PRIV_PROTOCOL
					+ ". Only following options are allowed:"
					+ " DES, 3DES, AES128, AES192, AES256.");
		}

		return oid;
	}

	private int getSecurityLevel(final String property) {
		int secLevel = -1;
		if ("noAuthNoPriv".equals(property)) {
			secLevel = SecurityLevel.NOAUTH_NOPRIV;
		} else if ("authNoPriv".equals(property)) {
			secLevel = SecurityLevel.AUTH_NOPRIV;
		} else if ("authPriv".equals(property)) {
			secLevel = SecurityLevel.AUTH_PRIV;
		} else {
			throw new IllegalArgumentException("Wrong value " + property
					+ " for property " + SnmpConstants.USER_SECURITY_LEVEL
					+ ". Only following options are allowed:"
					+ " noAuthNoPriv, authNoPriv, authPriv.");
		}

		return secLevel;
	}

	private int getSecurityModel(final String property) {
		int secModel = -1;
		if ("ANY".equals(property)) {
			secModel = SecurityModel.SECURITY_MODEL_ANY;
		} else if ("SNMPv1".equals(property)) {
			secModel = SecurityModel.SECURITY_MODEL_SNMPv1;
		} else if ("SNMPv2c".equals(property)) {
			secModel = SecurityModel.SECURITY_MODEL_SNMPv2c;
		} else if ("USM".equals(property)) {
			secModel = SecurityModel.SECURITY_MODEL_USM;
		} else {
			throw new IllegalArgumentException("Wrong value " + property
					+ " for property " + SnmpConstants.USER_SECURITY_LEVEL
					+ ". Only following options are allowed:"
					+ " ANY, SNMPv1, SNMPv2c, USM.");
		}

		return secModel;
	}

	private OctetString getTagList(final String property) {
		OctetString tagList = null;
		if (SnmpConstants.TRAP.equals(property)
				|| SnmpConstants.INFORM.equals(property)) {
			tagList = new OctetString(property);
		} else {
			throw new IllegalArgumentException("Wrong value " + property
					+ " for property " + SnmpConstants.TRAP_RECEIVER_TAG
					+ ". Only following options are allowed: "
					+ SnmpConstants.TRAP + ", " + SnmpConstants.INFORM + ".");
		}
		return tagList;
	}

	private OID getTransportDomain(final String property) {
		OID domain = null;
		if ("udpIpv4".equals(property)) {
			domain = TransportDomains.transportDomainUdpIpv4;
		} else if ("udpIpv6".equals(property)) {
			domain = TransportDomains.transportDomainUdpIpv6;
		} else if ("udpIpv4z".equals(property)) {
			domain = TransportDomains.transportDomainUdpIpv4z;
		} else if ("udpIpv6z".equals(property)) {
			domain = TransportDomains.transportDomainUdpIpv6z;
		} else if ("tcpIpv4".equals(property)) {
			domain = TransportDomains.transportDomainTcpIpv4;
		} else if ("tcpIpv6".equals(property)) {
			domain = TransportDomains.transportDomainTcpIpv6;
		} else if ("tcpIpv4z".equals(property)) {
			domain = TransportDomains.transportDomainTcpIpv4z;
		} else if ("tcpIpv6z".equals(property)) {
			domain = TransportDomains.transportDomainUdpIpv6z;
		} else {
			throw new IllegalArgumentException("Wrong value " + property
					+ " for property " + SnmpConstants.TRAP_RECEIVER_TDOMAIN
					+ ". Only following domains are allowed:"
					+ " udpIpv4, udpIpv6, udpIpv4z, udpIpv6z,"
					+ " tcpIpv4, tcpIpv6, tcpIpv4z, tcpIpv6z.");
		}
		return domain;
	}

	@Override
	@PostConstruct
	public void init() throws IOException {
		if (sysDescr == null || sysDescr.isEmpty()) {
			sysDescr = System.getProperty("os.name", "") + " - "
					+ System.getProperty("os.arch") + " - "
					+ System.getProperty("os.version");
		}
		setSysDescr(new OctetString(sysDescr));
		setSysOID(SYS_OID);
		setSysServices(new Integer32(sysServices));
		super.init();
		setSysProperties();
		getServer().addContext(new OctetString(communityContextName));
		notificationOriginator = new NotificationOriginatorImpl(
				new AsyncSession(session), vacmMIB, snmpv2MIB.getSysUpTime(),
				snmpTargetMIB, snmpNotificationMIB, snmpCommunityMIB);
		snmpv2MIB.setNotificationOriginator(agent);
		if (!trapReceivers.isEmpty()) {
			initAlertEventListener();
		}
		finishInit();
		run();
		sendColdStartNotification();
	}

	@SuppressWarnings("serial")
	private void initAlertEventListener() {
		internalEventBroadcaster.subscribe(new QoSEventListener() {
			@Override
			public void onServerEvent(final AbstractEvent event) {
				final AlertUpdateEvent updateEvent = (AlertUpdateEvent) event;
				final MAlert alert = snmpService.getAlertById(updateEvent.getAlertId());
				final UpdateType updateType = updateEvent.getUpdateType();
                if (updateType == UpdateType.NEW) {
                    sendAlertNotification(QligentVisionMib.oidVisionAlarmActiveState, alert);
                } else if (updateType.isCleared()) {
                    sendAlertNotification(QligentVisionMib.oidVisionAlarmClearState, alert);
                } else if (updateType.isSeverityChanged()
                        || updateType == UpdateType.UPDATE
                        || updateType == UpdateType.REPEAT) {
                    sendAlertNotification(QligentVisionMib.oidVisionAlarmUpdateState, alert);
                }
			}
		}, new QoSEventFilter() {
			@Override
			public boolean accept(final AbstractEvent event) {
				return event instanceof AlertUpdateEvent;
			}
		});
	}


    @Override
	protected void initTransportMappings() throws IOException {
		final String addr = "0.0.0.0/" + snmpPort;
		transportMappings = new TransportMapping[1];
		transportMappings[0] = new DefaultUdpTransportMapping(new UdpAddress(
				addr));
	}

	@Override
	protected void registerManagedObjects() {
		entityMib = new EntityMib(moFactory);
		visionMib = new QligentVisionMib(moFactory);
		try {
			entityMib.registerMOs(getServer(), null);
		} catch (final DuplicateRegistrationException e) {
			LOGGER.warn("Duplicate registration of entityMib");
		}
		try {
			visionMib.registerMOs(getServer(), null);
		} catch (final DuplicateRegistrationException e) {
			LOGGER.warn("Duplicate registration of visionMib");
		}
		try {
			server.register(createStaticIfTable(), null);
		} catch (final DuplicateRegistrationException e) {
			LOGGER.warn("Duplicate registration of ifTable");
		}
	}

    private void sendAlertNotification(final OID notificationOID,
			final MAlert alert) {
		sendNotification(notificationOID,
				EntityConverter.convertToTrapVariableBindings(alert));
	}

	@Override
	protected void sendColdStartNotification() {
		sendNotification(org.snmp4j.mp.SnmpConstants.coldStart,
				new VariableBinding[0]);
	}

	private void sendNotification(final OID notificationOID,
			final VariableBinding[] variableBindings) {
		agent.notify(new OctetString(communityContextName), notificationOID,
				variableBindings);
	}

	private void setSysProperties() {
		if (!sysContact.isEmpty()) {
			getSnmpv2MIB().setContact(new OctetString(sysContact));
		}
		if (!sysName.isEmpty()) {
			getSnmpv2MIB().setName(new OctetString(sysName));
		}
		if (!sysLocation.isEmpty()) {
			getSnmpv2MIB().setLocation(new OctetString(sysLocation));
		}
	}

	@Override
	protected void unregisterManagedObjects() {
		entityMib.unregisterMOs(getServer(), null);
		visionMib.unregisterMOs(getServer(), null);
	}
}