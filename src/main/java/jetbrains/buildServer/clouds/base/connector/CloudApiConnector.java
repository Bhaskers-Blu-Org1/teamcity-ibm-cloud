/*
 *
 *  * Copyright 2000-2014 JetBrains s.r.o.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package jetbrains.buildServer.clouds.base.connector;

import java.util.Collection;
import java.util.Map;
import jetbrains.buildServer.clouds.CloudException;
import jetbrains.buildServer.clouds.InstanceStatus;
import jetbrains.buildServer.clouds.base.AbstractCloudImage;
import jetbrains.buildServer.clouds.base.AbstractCloudInstance;
import jetbrains.buildServer.clouds.base.errors.CheckedCloudException;
import jetbrains.buildServer.clouds.base.errors.TypedCloudErrorInfo;
import org.jetbrains.annotations.NotNull;

/**
 * @author Sergey.Pak
 *         Date: 7/23/2014
 *         Time: 3:26 PM
 */
public interface CloudApiConnector<T extends AbstractCloudImage, G extends AbstractCloudInstance> {

  void test() throws CheckedCloudException;

  @NotNull
  InstanceStatus getInstanceStatus(@NotNull final G instance);

  @NotNull
  Map<String, ? extends AbstractInstance> listImageInstances(@NotNull final T image) throws CheckedCloudException;

  @NotNull
  TypedCloudErrorInfo[] checkImage(@NotNull final T image);

  @NotNull
  TypedCloudErrorInfo[] checkInstance(@NotNull final G instance);
}
