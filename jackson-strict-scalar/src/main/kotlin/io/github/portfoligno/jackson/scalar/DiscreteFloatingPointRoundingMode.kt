package io.github.portfoligno.jackson.scalar

enum class DiscreteFloatingPointRoundingMode {
  /**
   * Handle discrete floating-point number types as is and write out decimals in their precise forms
   * (not shortened forms as in [java.lang.Float#toString] or [java.lang.Double#toString]).
   *
   * This is the most conservative option.
   * The deserializer will raise an error when a decimal is not precisely representable.
   *
   * For example, deserializing `0.1` into a `Float` is an error with this option.
   * Its nearest approximation, which is, in this case, `0.100000001490116119384765625`, will not be applied.
   */
  UNNECESSARY,

  /**
   * Handle discrete floating-point number types as if they were continuous domains and write out decimals
   * in their precise forms (not shortened forms as in [java.lang.Float#toString] or [java.lang.Double#toString]).
   *
   * Precision is lost when a decimal is not precisely representable.
   */
  HALF_EVEN,

  /**
   * Handle discrete floating-point number types as is and write out decimals
   * in their canonical shortened forms (as in [java.lang.Float#toString] or [java.lang.Double#toString]).
   *
   * The is similar to the `Unnecessary` option, except that a shortened form is used in place of its precise form.
   * And the precise form is, in turn, an error if it is different from the shortened form.
   *
   * For example, deserializing `0.100000001490116119384765625` into a `Float` is an error with this option,
   * despite, it is more accurate than its shortened form `0.1`.
   */
  SHORTEN_EXCLUSIVE,

  /**
   * Handle discrete floating-point number types as if they were continuous domains and write out decimals
   * in their canonical shortened forms (as in [java.lang.Float#toString] or [java.lang.Double#toString]).
   *
   * This is the default behavior in Jackson. A value shift happens
   * when a given decimal is not a possible shortened form of the target floating-point number type.
   */
  HALF_EVEN_SHORTEN_HYBRID
}
