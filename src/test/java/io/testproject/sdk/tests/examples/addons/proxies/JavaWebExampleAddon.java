/*
 * Copyright (c) 2020 TestProject LTD. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.testproject.sdk.tests.examples.addons.proxies;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.testproject.sdk.internal.addons.ActionProxy;
import io.testproject.sdk.internal.addons.ProxyDescriptor;

/**
 * Proxy for Java+Web+Example+Addon Addon.
 */
public final class JavaWebExampleAddon {

  /**
   * Private constructor for a utility class.
   */
  private JavaWebExampleAddon() { }

  /**
   * Factory method for ClearFieldsAction.
   * @return ClearFieldsAction instance.
   */
  public static ClearFieldsAction getClearFieldsAction() {
    return new ClearFieldsAction();
  }

  /**
   * Factory method for TypeRandomPhoneAction.
   * @return TypeRandomPhoneAction instance.
   */
  public static TypeRandomPhoneAction getTypeRandomPhoneAction() {
    return new TypeRandomPhoneAction();
  }

  /**
   * Factory method for TypeRandomPhoneAction.
   * @param countryCode Country code prefix code for the random phone number.
   * @param maxDigits maximum amount of digits to include in the random phone number.
   * @return TypeRandomPhoneAction instance.
   */
  public static TypeRandomPhoneAction typeRandomPhoneAction(final String countryCode,
                                                            final int maxDigits) {
    return new TypeRandomPhoneAction(countryCode, maxDigits);
  }

  /**
   * Clear Fields Action Proxy class.
   */
  public static class ClearFieldsAction extends ActionProxy {

    /**
     * Constructor.
     */
    public ClearFieldsAction() {
      this.setDescriptor(new ProxyDescriptor("GrQN1LQqTEmuYTnIujiEwA",
              "io.testproject.examples.sdk.actions.ClearFieldsAction"));
    }
  }

  /**
   * Type Random Phone Number Action Proxy class.
   */
  public static class TypeRandomPhoneAction extends ActionProxy {
    /**
     * (INPUT).
     */
    private String countryCode;

    /**
     * (INPUT).
     */
    private int maxDigits;

    /**
     * (OUTPUT).
     */
    @SuppressFBWarnings(
            value = "UWF_UNWRITTEN_FIELD",
            justification = "It will be populated using reflection")
    private String phone;

    /**
     * Getter for {@link #countryCode} field.
     * @return Country code prefix code for the random phone number.
     */
    public String getCountryCode() {
      return countryCode;
    }

    /**
     * Getter for {@link #maxDigits} field.
     * @return maximum amount of digits to include in the random phone number.
     */
    public int getMaxDigits() {
      return maxDigits;
    }

    /**
     * Getter for {@link #phone} field.
     * @return Phone number generated.
     */
    public String getPhone() {
      return phone;
    }

    /**
     * Constructor.
     */
    public TypeRandomPhoneAction() {
      this.setDescriptor(new ProxyDescriptor("GrQN1LQqTEmuYTnIujiEwA",
              "io.testproject.examples.sdk.actions.TypeRandomPhoneAction"));
    }

    /**
     * Extended constructor with all input parameters.
     * @param countryCode Country code prefix code for the random phone number.
     * @param maxDigits maximum amount of digits to include in the random phone number.
     */
    public TypeRandomPhoneAction(final String countryCode, final int maxDigits) {
      this();
      this.countryCode = countryCode;
      this.maxDigits = maxDigits;
    }
  }
}
