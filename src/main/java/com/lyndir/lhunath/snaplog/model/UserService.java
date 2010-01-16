/*
 *   Copyright 2010, Maarten Billemont
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.lyndir.lhunath.snaplog.model;

import com.lyndir.lhunath.snaplog.data.LinkID;
import com.lyndir.lhunath.snaplog.data.User;


/**
 * <h2>{@link UserService}<br>
 * <sub>Service to manage and access {@link User}s.</sub></h2>
 * 
 * <p>
 * <i>Jan 9, 2010</i>
 * </p>
 * 
 * @author lhunath
 */
public interface UserService {

    User registerUser(LinkID linkID, String name);

    User findExistingUserWithLinkID(LinkID linkID);
}