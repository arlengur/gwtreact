package com.tecomgroup.qos.gwt.client.messages;

import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * Текстовые поля для отображения значений
 * 
 * @author abondin
 * 
 */
public interface FormattedResultMessages {
	/**
	 * Реализация, использующая {@link QoSMessages}
	 * 
	 * @author abondin
	 * 
	 */
	public static class DefaultFormattedResultMessages
			extends
				AbstractLocalizedMessages implements FormattedResultMessages {

		/**
		 * @param messages
		 */
		public DefaultFormattedResultMessages(final QoSMessages messages) {
			super(messages);
		}

		@Override
		public String no() {
			return messages.actionNo();
		}

		@Override
		public String unknown() {
			return messages.unknown();
		}

		@Override
		public String yes() {
			return messages.actionYes();
		}

	}
	/**
	 * For false(0.0)
	 * 
	 * @return
	 */
	String no();
	/**
	 * For null or NaN
	 * 
	 * @return
	 */
	String unknown();

	/**
	 * For true(1.0)
	 * 
	 * @return
	 */
	String yes();
}