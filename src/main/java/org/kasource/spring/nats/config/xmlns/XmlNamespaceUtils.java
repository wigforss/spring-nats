package org.kasource.spring.nats.config.xmlns;

import java.util.function.Function;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.w3c.dom.Element;

public final class XmlNamespaceUtils {

    private XmlNamespaceUtils() {
    }

    /**
     * Set the attribute value from element as a property value on bean if any value is present in the element.
     *
     * The property name on the bean is resolved by camel casing the attribute (and removing aný separator such as - or .
     *
     * @param attribute The name of the attribute to get from the element.
     * @param element   The element (XML) to get the attribute value from.
     * @param bean      The bean to set the property value of.
     **/
    public static void setValueIfPresent(String attribute,
                                         Element element,
                                         BeanDefinitionBuilder bean) {
        String value = element.getAttribute(attribute);
        if (!StringUtils.isEmpty(value)) {
            String propertyName = CaseUtils.toCamelCase(attribute, false, '-', '.');
            bean.addPropertyValue(propertyName, value);
        }
    }

    /**
     * Set the attribute value from element as a property value on bean if any value is present in the element.
     *
     * @param attribute     The name of the attribute to get from the element.
     * @param propertyName  Name of the property of the bean to set
     * @param element       The element (XML) to get the attribute value from.
     * @param bean          The bean to set the property value of.
     **/
    public static void setValueIfPresent(String attribute,
                                         String propertyName,
                                         Element element,
                                         BeanDefinitionBuilder bean) {
        String value = element.getAttribute(attribute);
        if (!StringUtils.isEmpty(value)) {
            bean.addPropertyValue(propertyName, value);
        }
    }

    /**
     * Set the attribute value from element as a property value on bean if any value is present in the element.
     *
     * The property name on the bean is resolved by camel casing the attribute (and removing aný separator such as - or .
     *
     * @param attribute The name of the attribute to get from the element.
     * @param element   The element (XML) to get the attribute value from.
     * @param bean      The bean to set the property value of.
     * @param function  Transforms the attribute value before setting it as a property value.
     **/
    public static void setValueIfPresent(String attribute,
                                         Element element,
                                         BeanDefinitionBuilder bean,
                                         Function<String, Object> function) {
        String value = element.getAttribute(attribute);
        if (!StringUtils.isEmpty(value)) {
            String propertyName = CaseUtils.toCamelCase(attribute, false, '-', '.');
            bean.addPropertyValue(propertyName, function.apply(value));
        }
    }

    /**
     * Set the attribute value from element as a property value on bean if any value is present in the element.
     *
     * @param attribute     The name of the attribute to get from the element.
     * @param propertyName  Name of the property of the bean to set
     * @param element       The element (XML) to get the attribute value from.
     * @param bean          The bean to set the property value of.
     * @param function      Transforms the attribute value before setting it as a property value.
     **/
    public static void setValueIfPresent(String attribute,
                                         String propertyName,
                                         Element element,
                                         BeanDefinitionBuilder bean,
                                         Function<String, Object> function) {
        String value = element.getAttribute(attribute);
        if (!StringUtils.isEmpty(value)) {
            bean.addPropertyValue(propertyName, function.apply(value));
        }
    }

    /**
     * Set the bean name of a property reference of bean if the attribute is set in the element.
     *
     * The property name on the bean is resolved by camel casing the attribute (and removing aný separator such as - or .
     *
     * @param attribute The name of the attribute to get the bean name from.
     * @param element   The element(XML) to get the attribute value from.
     * @param bean      The bean to set the property reference of.
     */
    public static void setReferenceIfPresent(String attribute,
                                             Element element,
                                             BeanDefinitionBuilder bean) {
        String beanName = element.getAttribute(attribute);
        if (!StringUtils.isEmpty(beanName)) {
            String propertyName = CaseUtils.toCamelCase(attribute, false, '-', '.');
            bean.addPropertyReference(propertyName, beanName);
        }
    }
}
