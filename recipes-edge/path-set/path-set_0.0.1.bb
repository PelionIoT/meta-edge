SUMMARY = "Adds needed paths to the profile for Izuma Edge programs"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "file://edgepath.sh"

PR = "r0"

S = "${WORKDIR}"

FILES:${PN} = "${sysconfdir}"

do_install() {
    install -d ${D}${sysconfdir}/profile.d
    install -m 0755 edgepath.sh ${D}${sysconfdir}/profile.d
}


