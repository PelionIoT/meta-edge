# The standard parsec-tool recipe provided in meta-security no longer works
# with the rust compiler version provided in LmP v91 - the parsec-tool binary
# is written to a different folder.
#
# This recipe replaces the entire do_install function of the base recipe, and
# uses the CARGO_TARGET_SUBDIR folder instead.

do_install() {
  install -d ${D}/${bindir}
  install -m 755 "${B}/target/${CARGO_TARGET_SUBDIR}/parsec-tool" "${D}${bindir}/parsec-tool"
  install -m 755 "${S}/tests/parsec-cli-tests.sh" "${D}${bindir}/parsec-cli-tests.sh"
}
