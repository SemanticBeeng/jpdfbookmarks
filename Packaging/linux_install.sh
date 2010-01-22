#!/bin/bash

#
# linux_install.sh
# 
# Copyright (c) 2010 Flaviano Petrocchi <flavianopetrocchi at gmail.com>.
# All rights reserved.
# 
# This file is part of JPdfBookmarks.
# 
# JPdfBookmarks is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
# 
# JPdfBookmarks is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License
# along with JPdfBookmarks.  If not, see <http://www.gnu.org/licenses/>.
#

if [[ $(/usr/bin/id -u) -ne 0 ]]; then
    echo "To run this script you must be root!"
    exit
fi

SCRIPT_DIR=$(cd $(dirname "$0"); pwd)
DIR_IN_PATH=/usr/local/bin
INSTALL_PATH=/usr/local/lib
VERSION=$(cat ${SCRIPT_DIR}/VERSION)
NAME=jpdfbookmarks-${VERSION}
INSTALL_DIR=${INSTALL_PATH}/${NAME}

mkdir ${INSTALL_DIR}
cp -r ${SCRIPT_DIR}/* ${INSTALL_DIR}/  

chmod +x ${INSTALL_DIR}/link_this_in_linux_path.sh
chmod +x ${INSTALL_DIR}/link_this_in_linux_path_cli.sh

ln -s ${INSTALL_DIR}/link_this_in_linux_path.sh ${DIR_IN_PATH}/jpdfbookmarks_gui
ln -s ${INSTALL_DIR}/link_this_in_linux_path_cli.sh ${DIR_IN_PATH}/jpdfbookmarks

