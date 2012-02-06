/**
 * This file is part of the grappendorf.net CoYoHo Control Your Home System.
 *
 * If you use the code for your own work, either in it's original or in a
 * modified form, you must make sure that you leave the original author's
 * name or say that your work is based on his.
 *
 * The contents of this file are subject to the Apache License Version
 * 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is grappendorf.net CoYoHo / Server
 *
 * The Initial Developer of the Original Code is
 * Dirk Grappendorf (www.grappendorf.net)
 * Portions created by the Initial Developer are Copyright (C) 2011
 * the Initial Developer. All Rights Reserved.
 */

package de.iritgo.aktera.util.system;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;


public class AnnotationUtils
{
	public static boolean fieldHasAnnotation(Object bean, String fieldName, Class annotationClass)
	{
		try
		{
			if (bean != null)
			{
				Field field = bean.getClass().getDeclaredField(fieldName);
				if (field != null)
				{
					return field.getAnnotation(annotationClass) != null;
				}
			}
		}
		catch (Exception returnFalse)
		{
		}
		return false;
	}

	public static Object getClassAnnotationAttribute(Object bean, Class annotationClass, String attribute)
	{
		if (bean != null)
		{
			Annotation annotation = bean.getClass().getAnnotation(annotationClass);
			if (annotation != null)
			{
				return org.springframework.core.annotation.AnnotationUtils.getAnnotationAttributes(annotation).get(
								attribute);
			}
		}
		return null;
	}

	public static Object getFieldAnnotationAttribute(Object bean, String fieldName, Class annotationClass, String attribute)
	{
		try
		{
			if (bean != null)
			{
				Field field = bean.getClass().getDeclaredField(fieldName);
				if (field != null)
				{
					Annotation annotation = field.getAnnotation(annotationClass);
					if (annotation != null)
					{
						return org.springframework.core.annotation.AnnotationUtils.getAnnotationAttributes(annotation)
										.get(attribute);
					}
				}
			}
		}
		catch (Exception returnNull)
		{
		}
		return null;
	}
}
