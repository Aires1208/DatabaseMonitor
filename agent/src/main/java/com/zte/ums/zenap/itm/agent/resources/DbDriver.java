/**
 * Copyright (C) 2015 ZTE, Inc. and others. All rights reserved. (ZTE)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.zte.ums.zenap.itm.agent.resources;

/**
 * @author wangjiangping
 * @date 2016/4/29 10:11:46
 * @description database driver register class. 
 */
public class DbDriver {
	public static String MySQL = "mysql";
	public static String SQLServer = "sqlserver";
			
	public static synchronized void register(String dbType) throws ClassNotFoundException {
		if(MySQL.equalsIgnoreCase(dbType)){
			Class.forName("com.mysql.jdbc.Driver");
			return;
		}
		if(SQLServer.equalsIgnoreCase(dbType)){
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			return;
		}
	}
}
