DESCRIPTION="edge-core"

LICENSE="Apache-2.0"
LIC_FILES_CHKSUM = "file://${WORKDIR}/git/LICENSE;md5=1dece7821bf3fd70fe1309eaa37d52a2"

PROVIDES += " edge-core edge-core-dbg "
RPROVIDES:${PN} += " edge-core edge-core-dbg "

# Patches for quilt goes to files directory
FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRCREV = "${EDGE_CORE_VERSION}"
PV="${SRCREV}+git${SRCPV}"

RM_WORK_EXCLUDE += "${PN}"

SRC_URI = "gitsm://git@github.com/PelionIoT/mbed-edge.git;protocol=https;branch=master \
           file://target.cmake \
           file://sotp_fs_yocto.h \
           file://pal_plat_yocto.c \
           file://toolchain.cmake \
           file://mbed_cloud_client_user_config.h \
           file://deploy_ostree_delta_update.sh \
           file://0001-disable-Doxygen.patch \
           file://0001-parsec-se-driver-should-be-compiled-separately-in-Yo.patch \
           file://0001-Temporary-fix-for-trace-mutex-lock-issue.patch \
           file://0001-fix_psa_storage_location.patch \
           file://0008-ordered-reboot.patch "

SRC_URI += "\
    ${@bb.utils.contains('MBED_EDGE_CORE_CONFIG_DEVELOPER_MODE','ON','file://mbed_cloud_dev_credentials.c','',d)} \
    ${@bb.utils.contains('MBED_EDGE_CORE_CONFIG_DEVELOPER_MODE','ON','file://update_default_resources.c','',d)} \
    ${@bb.utils.contains('MBED_EDGE_CORE_CONFIG_BYOC_MODE','OFF','file://edge-core.service','',d)} \
    ${@bb.utils.contains('MBED_EDGE_CORE_CONFIG_BYOC_MODE','ON','file://edge-core-byoc.service','',d)} \
    ${@bb.utils.contains('MBED_EDGE_CORE_CONFIG_BYOC_MODE','ON','file://launch-byoc-edge-core.sh','',d)} \
"

DEPENDS = " libcap mosquitto mercurial-native curl python3-native python3-pip-native python3 python3-setuptools-native python3-setuptools-scm"
DEPENDS += "${@ 'parsec-se-driver' if d.getVar('MBED_EDGE_CORE_CONFIG_PARSEC_TPM_SE_SUPPORT') == 'ON' else ' '}"

RDEPENDS:${PN} = " procps bash tar bzip2 rng-tools glibc-utils"
RDEPENDS:${PN} += "${@ 'edge-tool' if d.getVar('MBED_EDGE_CORE_CONFIG_BYOC_MODE') == 'ON' else ' '}"

# Installed packages
PACKAGES = "${PN} ${PN}-dbg"
FILES:${PN} += "/edge \
                /edge/mbed \
                /edge/mbed/edge-core \
                /userdata"

FILES:${PN}-dbg += "/edge/mbed/debug \
                    /usr/src/debug/mbed-edge"

wbindir= "/edge/system/bin"

S = "${WORKDIR}/git"

inherit cmake update-rc.d systemd python3native pkgconfig edge_versions
# Default configuration flags for mbed-edge
MBED_EDGE_CORE_CONFIG_TRACE_LEVEL ?= "WARN"
MBED_EDGE_CORE_CONFIG_FIRMWARE_UPDATE ?= "ON"
MBED_EDGE_CORE_CONFIG_FOTA_ENABLE ?= "ON"
MBED_EDGE_CORE_CONFIG_FOTA_TRACE ?= "ON"
MBED_EDGE_CORE_CONFIG_FOTA_COMBINED_IMAGE_SUPPORT ?= "ON"
MBED_EDGE_CORE_CONFIG_CURL_DYNAMIC_LINK ?= "ON"
MBED_EDGE_CORE_CONFIG_DEVELOPER_MODE ?= "ON"
MBED_EDGE_CORE_CONFIG_FACTORY_MODE ?= "OFF"
MBED_EDGE_CORE_CONFIG_BYOC_MODE ?= "OFF"
MBED_EDGE_CORE_CONFIG_PARSEC_TPM_SE_SUPPORT ?= "OFF"
MBED_EDGE_CORE_CONFIG_CUSTOM_PORT ?= "OFF"
MBED_EDGE_CMAKE_BUILD_TYPE ?= "Debug"

lcl_maybe_fortify = '-D_FORTIFY_SOURCE=0'

EXTRA_OECMAKE += " \
    -DCMAKE_BUILD_TYPE=${MBED_EDGE_CMAKE_BUILD_TYPE} \
    -DTARGET_TOOLCHAIN=yocto \
    -DMBED_CONF_MBED_TRACE_ENABLE=1 \
    -DTARGET_CONFIG_ROOT=${WORKDIR} \
    -DCLOUD_CLIENT_CONFIG=${WORKDIR}/mbed_cloud_client_user_config.h \
    -DTRACE_LEVEL=${MBED_EDGE_CORE_CONFIG_TRACE_LEVEL} \
    -DFIRMWARE_UPDATE=${MBED_EDGE_CORE_CONFIG_FIRMWARE_UPDATE} \
    -DMBED_CLOUD_CLIENT_CURL_DYNAMIC_LINK=${MBED_EDGE_CORE_CONFIG_CURL_DYNAMIC_LINK} \
    -DFOTA_ENABLE=${MBED_EDGE_CORE_CONFIG_FOTA_ENABLE} \
    -DFOTA_TRACE=${MBED_EDGE_CORE_CONFIG_FOTA_TRACE} \
    -DFOTA_COMBINED_IMAGE_SUPPORT=${MBED_EDGE_CORE_CONFIG_FOTA_COMBINED_IMAGE_SUPPORT} \
    -DFOTA_SCRIPT_DIR=\"/edge/mbed\" \
    -DFOTA_INSTALL_MAIN_SCRIPT=\"deploy_ostree_delta_update.sh\" \
    -DBOOT_CAPSULE_UPDATE_DIR=\"/boot/efi/EFI/UpdateCapsule\" \
    -DBOOT_CAPSULE_UPDATE_FILENAME=\"u-boot-caps.bin\" \
    -DDEVELOPER_MODE=${MBED_EDGE_CORE_CONFIG_DEVELOPER_MODE} \
    -DFACTORY_MODE=${MBED_EDGE_CORE_CONFIG_FACTORY_MODE} \
    -DBYOC_MODE=${MBED_EDGE_CORE_CONFIG_BYOC_MODE} \
    -DCUSTOM_PORT=${MBED_EDGE_CORE_CONFIG_CUSTOM_PORT} \
    -DPARSEC_TPM_SE_SUPPORT=${MBED_EDGE_CORE_CONFIG_PARSEC_TPM_SE_SUPPORT} \
    ${MBED_EDGE_CUSTOM_CMAKE_ARGUMENTS} \
    "

INITSCRIPT_NAME = "edge-core"
INITSCRIPT_PARAMS = "defaults 85 15"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "edge-core.service"

# Enable network access for downloading libcurl/parsec-se-driver etc sources.
do_configure[network] = "1"

do_configure:prepend() {
    cd ${S}

    [ -f "${WORKDIR}/mbed_cloud_dev_credentials.c" ] && {
        mv "${WORKDIR}/mbed_cloud_dev_credentials.c" config/
    }
    [ -f "${WORKDIR}/update_default_resources.c" ] && {
        mv "${WORKDIR}/update_default_resources.c" config/
    }

    mkdir -p lib/mbed-cloud-client/mbed-client-pal/Source/Port/Reference-Impl/OS_Specific/Linux/Board_Specific/TARGET_yocto
    cp ${WORKDIR}/pal_plat_yocto.c \
       lib/mbed-cloud-client/mbed-client-pal/Source/Port/Reference-Impl/OS_Specific/Linux/Board_Specific/TARGET_yocto/pal_plat_yocto.c

    export HTTP_PROXY=${HTTP_PROXY}
    export HTTPS_PROXY=${HTTPS_PROXY}
    cd ${B}
}

do_compile:prepend() {

    if [ "${MBED_EDGE_CORE_CONFIG_PARSEC_TPM_SE_SUPPORT}" = "ON" ]; then
        if [ "${MBED_EDGE_CMAKE_BUILD_TYPE}" = "Debug" ]; then
            mkdir -p ${S}/lib/pal-platform/Middleware/parsec_se_driver/parsec_se_driver/target/debug
            cp ${PKG_CONFIG_SYSROOT_DIR}/usr/lib/libparsec_se_driver.a ${S}/lib/pal-platform/Middleware/parsec_se_driver/parsec_se_driver/target/debug/
        else
            mkdir -p ${S}/lib/pal-platform/Middleware/parsec_se_driver/parsec_se_driver/target/release
            cp ${PKG_CONFIG_SYSROOT_DIR}/usr/lib/libparsec_se_driver.a ${S}/lib/pal-platform/Middleware/parsec_se_driver/parsec_se_driver/target/release/
        fi
    fi
}

do_install() {

    install -d "${D}${systemd_system_unitdir}"

    if [ "${MBED_EDGE_CORE_CONFIG_BYOC_MODE}" = "ON" ]; then
        install -d "${D}${wbindir}"
        install -m 755 "${WORKDIR}/edge-core-byoc.service" "${D}${systemd_system_unitdir}/edge-core.service"
        install -m 755 "${WORKDIR}/launch-byoc-edge-core.sh" "${D}${wbindir}/"
    else
        install -m 755 "${WORKDIR}/edge-core.service" "${D}${systemd_system_unitdir}/edge-core.service"
    fi

    install -d "${D}/edge/mbed"
    install "${WORKDIR}/build/bin/edge-core" "${D}/edge/mbed"

    install -d "${D}/etc/tmpfiles.d"
    echo "d /var/rootdirs/userdata 0755 root root -" >> "${D}/etc/tmpfiles.d/userdata-tmpfiles.conf"
    ln -sf "/var/rootdirs/userdata" "${D}/userdata"

    if [ "${MBED_EDGE_CORE_CONFIG_FOTA_ENABLE}" = "ON" ]; then
        install -m 755 "${WORKDIR}/deploy_ostree_delta_update.sh" "${D}/edge/mbed"
    fi
}
