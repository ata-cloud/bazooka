/*
 *    Copyright 2018-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package net.atayun.bazooka.deploy.biz.constants;

/**
 * @author Ping
 * @date 2019-07-30
 */
public final class JenkinsCallbackConstants {

    private JenkinsCallbackConstants() {
        throw new AssertionError("Must not instantiate constant utility class");
    }

    public static final String BUILD_RESULT_SUCCESS = "SUCCESS";
    public static final String BUILD_RESULT_UNSTABLE = "UNSTABLE";
    public static final String BUILD_RESULT_FAILURE = "FAILURE";
    public static final String BUILD_RESULT_ABORTED = "NOT_BUILT";
    public static final String BUILD_RESULT_NOT_BUILT = "NOT_BUILT";

}
