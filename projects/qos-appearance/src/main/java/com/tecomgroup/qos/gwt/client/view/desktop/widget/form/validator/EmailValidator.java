package com.tecomgroup.qos.gwt.client.view.desktop.widget.form.validator;

import com.sencha.gxt.widget.core.client.form.validator.RegExValidator;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.gwt.client.i18n.ValidationMessages;

/**
 * @author meleshin.o
 */
public class EmailValidator extends RegExValidator {

    public EmailValidator(final ValidationMessages messages){
        super(MUser.EMAIL_VALID_PATTERN, messages.incorrectEmailFormat());
    }
}
