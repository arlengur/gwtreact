package com.tecomgroup.qos.gwt.client.model.policy;

/**
 * @author smyshlyaev.s
 */
public class SourceRow implements PolicyTreeGridRow {

	private String sourceKey;
	private String sourceDisplayName;

	public SourceRow() {
		super();
	}

	public SourceRow(final String sourceKey, final String sourceDisplayName) {
		this.sourceKey = sourceKey;
		this.sourceDisplayName = sourceDisplayName;
	}

	@Override
	public String getActionsTemplateName() {
		return "";
	}

	@Override
	public String getAgent() {
		return null;
	}

	@Override
	public String getConditionsTemplateName() {
		return "";
	}

	@Override
	public String getCriticalCease() {
		return "";
	}

	@Override
	public String getCriticalRaise() {
		return "";
	}

	@Override
	public String getKey() {
		return sourceKey;
	}

	@Override
	public String getName() {
		return getSourceDisplayName();
	}

	@Override
	public String getParameterDisplayName() {
		return "";
	}

	@Override
	public String getSourceDisplayName() {
		return sourceDisplayName;
	}

	@Override
	public String getWarningCease() {
		return "";
	}

	@Override
	public String getWarningRaise() {
		return "";
	}

	public void setSourceDisplayName(final String sourceDisplayName) {
		this.sourceDisplayName = sourceDisplayName;
	}

	public void setSourceKey(final String sourceKey) {
		this.sourceKey = sourceKey;
	}
}
