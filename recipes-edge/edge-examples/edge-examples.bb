DESCRIPTION="edge-examples"

LICENSE="Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

# Patches for quilt goes to files directory
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRCREV = "dd38486a8b776f6180a9f6d948b7e555b0fce42a"

SRC_URI = "git://git@github.com/PelionIoT/mbed-edge-examples.git;protocol=https;branch=master \
           file://pt-example \
           file://blept-example \
           file://blept-devices.json \
           file://0001-disable-doxygen.patch \
           file://0002-fix-libevent-build-with-CMake-in-Yocto.patch \
           file://0003-Set-optimization-level-to-0-only-if-FORTIFY_SOURCE-i.patch \
           file://mept-ble.init \
           "

# Installed packages
PACKAGES = "${PN} ${PN}-dbg"
FILES:${PN} += "/edge \
                /edge/mbed \
                /edge/mbed/pt-example \
                /edge/mbed/blept-example \
                /edge/mbed/blept-devices.json"

FILES:${PN}-dbg += "/edge/mbed/.debug \
                    /usr/src/debug/edge-examples"

S = "${WORKDIR}/git"

DEPENDS = " libcap mosquitto glib-2.0 mercurial-native"
RDEPENDS:${PN} = " procps bash bluez5 edge-core"

EXTRA_OECMAKE += " \
    -DTARGET_DEVICE=yocto \
    -DTARGET_TOOLCHAIN=yocto \
    -DCMAKE_BUILD_TYPE=Release \
    ${MBED_EDGE_CUSTOM_CMAKE_ARGUMENTS} "
inherit cmake update-rc.d pkgconfig

INITSCRIPT_NAME = "mept-ble"
INITSCRIPT_PARAMS = "defaults 86 15"

do_configure[network] = "1"

do_configure:prepend() {
    cd ${S}
    git submodule update --init --recursive
    cd ${B}
}

do_install() {
    install -d "${D}/edge/mbed"
    install "${WORKDIR}/build/bin/pt-example" "${D}/edge/mbed"
    install "${WORKDIR}/build/bin/blept-example" "${D}/edge/mbed"
    install "${WORKDIR}/blept-devices.json" "${D}/edge/mbed"
    install "${WORKDIR}/build/bin/mqttpt-example" "${D}/edge/mbed"
    install "${WORKDIR}/git/mqttpt-example/mqttgw_sim/mqtt_ep.sh" "${D}/edge/mbed"
    install "${WORKDIR}/git/mqttpt-example/mqttgw_sim/mqtt_gw.sh" "${D}/edge/mbed"

    install -d "${D}${sysconfdir}/logrotate.d"
    install -m 644 "${WORKDIR}/pt-example" "${D}${sysconfdir}/logrotate.d"
    install -m 644 "${WORKDIR}/blept-example" "${D}${sysconfdir}/logrotate.d"

    install -d "${D}${sysconfdir}/init.d/"
    install -m 755 "${WORKDIR}/mept-ble.init" "${D}${sysconfdir}/init.d/${INITSCRIPT_NAME}"
}
