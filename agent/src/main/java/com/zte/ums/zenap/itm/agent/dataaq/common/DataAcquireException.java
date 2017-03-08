/**
 * Copyright (C) 2015 ZTE, Inc. and others. All rights reserved. (ZTE)
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.zte.ums.zenap.itm.agent.dataaq.common;

public class DataAcquireException extends Exception {

    private static final long serialVersionUID = 8025328430879415200L;
    private int errorCode = 1;

    public DataAcquireException(String message) {
        super(message);
    }
    
    public DataAcquireException(int code, String message) {
        super(message);
        this.errorCode = code;
    }

    public DataAcquireException(Throwable cause, int code) {
        super(cause);
        this.errorCode = code;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
