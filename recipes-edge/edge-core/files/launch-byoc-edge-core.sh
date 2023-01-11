#!/bin/bash
# ----------------------------------------------------------------------------
# Copyright (c) 2021, Pelion Ltd.
#
# SPDX-License-Identifier: Apache-2.0
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# ----------------------------------------------------------------------------

set -e

CREDENTIALS_DIR="/userdata/mbed"
DEVELOPER_CERTIFICATE="${CREDENTIALS_DIR}/mbed_cloud_dev_credentials.c"
UPDATE_CERTIFICATE="${CREDENTIALS_DIR}/update_default_resources.c"
DEV_CBOR="${CREDENTIALS_DIR}/mbed_cloud_dev_credentials.cbor"

EDGE_CORE="/edge/mbed/edge-core"
EDGE_TOOL_CMD="/edge/mbed/edge-tool/edge_tool.py convert-dev-cert \
--development-certificate ${DEVELOPER_CERTIFICATE} \
--cbor ${DEV_CBOR} \
--update-resource ${UPDATE_CERTIFICATE}"

if [[ ! -f "$DEV_CBOR" ]]; then
    echo "CBOR file not found, checking if developer and update certificates are available..."
    if [[ -f "$DEVELOPER_CERTIFICATE" ]] && [[ -f "$UPDATE_CERTIFICATE" ]]; then
        echo "Found developer and update certificates. Generating CBOR using cmd - $EDGE_TOOL_CMD"
        $EDGE_TOOL_CMD
    else
        echo "Developer or update certificate not found at $CREDENTIALS_DIR. Device is not configured for Pelion Device Management!"
        exit 1
    fi
fi

echo "Found CBOR formatted credentials at $DEV_CBOR. Starting edge-core..."

exec $EDGE_CORE \
--cbor-conf $DEV_CBOR \
--http-port 9101
