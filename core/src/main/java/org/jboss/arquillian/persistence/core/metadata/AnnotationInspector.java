/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.persistence.core.metadata;

import org.jboss.arquillian.test.spi.TestClass;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
public class AnnotationInspector<T extends Annotation>
{

   private final Map<Method, T> annotatedMethods;

   private final TestClass testClass;

   private final Class<T> annotationClass;

   public AnnotationInspector(TestClass testClass, Class<T> annotationClass)
   {
      this.testClass = testClass;
      this.annotationClass = annotationClass;
      this.annotatedMethods = fetch(annotationClass);
   }

   public Collection<T> fetchAll()
   {
      final Set<T> all = new HashSet<T>();
      all.addAll(annotatedMethods.values());
      final T annotationOnClassLevel = getAnnotationOnClassLevel();
      if (annotationOnClassLevel != null)
      {
         all.add(annotationOnClassLevel);
      }
      return all;
   }

   public boolean isDefinedOn(Method method)
   {
      return fetchFrom(method) != null;
   }

   public boolean isDefinedOnAnyMethod()
   {
      return !annotatedMethods.isEmpty();
   }

   public T fetchFrom(Method method)
   {
      return annotatedMethods.get(method);
   }

   public boolean isDefinedOnClassLevel()
   {
      return getAnnotationOnClassLevel() != null;
   }

   public T getAnnotationOnClassLevel()
   {
      return testClass.getAnnotation(annotationClass);
   }

   /**
    *
    * Fetches annotation for a given test class. If annotation is defined on method
    * level it's returned as a result. Otherwise class level annotation is returned if present.
    *
    * @return T annotation or null if not found.
    *
    */
   public T fetchUsingFirst(Method testMethod)
   {
      T usedAnnotation = getAnnotationOnClassLevel();
      if (isDefinedOn(testMethod))
      {
         usedAnnotation = fetchFrom(testMethod);
      }

      return usedAnnotation;
   }

   // Private

   private Map<Method, T> fetch(Class<T> annotation)
   {
      final Map<Method, T> map = new HashMap<Method, T>();

      for (Method testMethod : testClass.getMethods(annotation))
      {
         map.put(testMethod, testMethod.getAnnotation(annotation));
      }

      return map;
   }
}

