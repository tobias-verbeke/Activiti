/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.workflow.simple.alfresco.conversion.form;

import org.activiti.workflow.simple.alfresco.configmodel.Form;
import org.activiti.workflow.simple.alfresco.configmodel.FormField;
import org.activiti.workflow.simple.alfresco.configmodel.FormFieldControl;
import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionConstants;
import org.activiti.workflow.simple.alfresco.conversion.AlfrescoConversionUtil;
import org.activiti.workflow.simple.alfresco.model.M2Mandatory;
import org.activiti.workflow.simple.alfresco.model.M2Property;
import org.activiti.workflow.simple.alfresco.model.M2Type;
import org.activiti.workflow.simple.converter.WorkflowDefinitionConversion;
import org.activiti.workflow.simple.definition.form.DatePropertyDefinition;
import org.activiti.workflow.simple.definition.form.FormPropertyDefinition;

public class AlfrescoDatePropertyConverter implements AlfrescoFormPropertyConverter {

	@Override
	public Class<? extends FormPropertyDefinition> getConvertedClass() {
		return DatePropertyDefinition.class;
	}

	@Override
	public void convertProperty(M2Type contentType, String formSet, Form form, FormPropertyDefinition propertyDefinition, WorkflowDefinitionConversion conversion) {
		DatePropertyDefinition dateDefinition = (DatePropertyDefinition) propertyDefinition;
		String propertyName = AlfrescoConversionUtil.getQualifiedName(AlfrescoConversionUtil.getModelNamespacePrefix(conversion),
				dateDefinition.getName());
		
		// Add to content model
		M2Property property = new M2Property();
		property.setMandatory(new M2Mandatory(dateDefinition.isMandatory()));
		property.setName(propertyName);
		
		if(dateDefinition.isShowTime()) {
			property.setPropertyType(AlfrescoConversionConstants.PROPERTY_TYPE_DATETIME);
		} else {
			property.setPropertyType(AlfrescoConversionConstants.PROPERTY_TYPE_DATE);
		}
		
		// Add form configuration
		form.getFormFieldVisibility().addShowFieldElement(propertyName);
		FormField formField = form.getFormAppearance().addFormField(propertyName, property.getTitle(), formSet);

		if(dateDefinition.isWritable()) {
			// Read-only properties should always be rendered using an info-template
			FormFieldControl control = new FormFieldControl();
			control.setTemplate(AlfrescoConversionConstants.FORM_READONLY_TEMPLATE);
			formField.setControl(control);
		} else {
			// Use custom date-control
			FormFieldControl control = new FormFieldControl();
			control.setTemplate(AlfrescoConversionConstants.FORM_DATE_TEMPLATE);
			control.addControlParameter(AlfrescoConversionConstants.FORM_DATE_PARAM_SHOW_TIME, 
					Boolean.toString(dateDefinition.isShowTime()));
			control.addControlParameter(AlfrescoConversionConstants.FORM_DATE_PARAM_SUBMIT_TIME, 
					Boolean.toString(dateDefinition.isShowTime()));
			formField.setControl(control);
		}
	}
}
