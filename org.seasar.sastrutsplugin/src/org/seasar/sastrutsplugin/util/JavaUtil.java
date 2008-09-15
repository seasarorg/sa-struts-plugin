/*
 * Copyright 2008 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package org.seasar.sastrutsplugin.util;

import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;

public class JavaUtil {
	
	/**
	 * Creates a qualified class name from a class name which doesn't contain package name.
	 * 
	 * @param parent a full qualified class name of the class which uses this variable
	 * @param type a class name which doesn't contain package name
	 * @return full a created qualified class name
	 */
	public static String getFullQName(IType parent,String type){
		if(type.indexOf('.') >= 0){
			return type;
		}
		if(isPrimitive(type)){
			return type;
		}
		IJavaProject project = parent.getJavaProject();
		try {
			IType javaType = project.findType("java.lang." + type);
			if(javaType!=null && javaType.exists()){
				return javaType.getFullyQualifiedName();
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		try {
			IType javaType = project.findType(parent.getPackageFragment().getElementName() + "." + type);
			if(javaType!=null && javaType.exists()){
				return javaType.getFullyQualifiedName();
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		try {
			IImportDeclaration[] imports = parent.getCompilationUnit().getImports();
			for(int i=0;i<imports.length;i++){
				String importName = imports[i].getElementName();
				if(importName.endsWith("." + type)){
					return importName;
				}
				if(importName.endsWith(".*")){
					try {
						IType javaType = project.findType(importName.replaceFirst("\\*$",type));
						if(javaType!=null && javaType.exists()){
							return javaType.getFullyQualifiedName();
						}
					} catch(Exception ex){
					}
				}
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return type;
	}
	
	/**
	 * This method judges whether the type is a primitive type. 
	 * 
	 * @param type type (classname or primitive type)
	 * @return 
	 * <ul>
	 *   <li>true - primitive type</li>
	 *   <li>false - not primitive type</li>
	 * </ul>
	 */
	public static boolean isPrimitive(String type){
		if(type.equals("int") || type.equals("long") || type.equals("double") || type.equals("float") || 
				type.equals("char") || type.equals("boolean") || type.equals("byte")){
			return true;
		}
		return false;
	}
}

