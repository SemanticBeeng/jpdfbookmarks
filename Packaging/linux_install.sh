#!/bin/bash

if [[ $(/usr/bin/id -u) -ne 0 ]]; then
    echo "To run this script you must be root!"
    exit
fi

SCRIPTDIR=$(cd $(dirname "$0"); pwd)

chmod +x ${SCRIPTDIR}/link_this_in_linux_path.sh
chmod +x ${SCRIPTDIR}/link_this_in_linux_path_cli.sh

ln -s ${SCRIPTDIR}/link_this_in_linux_path.sh /usr/bin/jpdfbookmarks_gui
ln -s ${SCRIPTDIR}/link_this_in_linux_path_cli.sh /usr/bin/jpdfbookmarks
 