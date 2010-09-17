#!/bin/sh

#
# packager.sh
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

SCRIPTDIR=$(cd $(dirname "$0"); pwd)
PREV_DIR=$(pwd)


cd ${SCRIPTDIR}

PROP_FILE=../jpdfbookmarks_core/src/it/flavianopetrocchi/jpdfbookmarks/jpdfbookmarks.properties
VERSION=$(sed '/^\#/d' ${PROP_FILE} | grep 'VERSION'  | tail -n 1 | sed 's/^.*=//')

NAME=jpdfbookmarks-${VERSION}
SRCNAME=jpdfbookmarks-src-${VERSION}

rm -f ${NAME}.zip
rm -f ${NAME}.tar
rm -f ${NAME}.tar.gz
rm -f -R ${NAME}

mkdir ${NAME}

cp jpdfbookmarks.exe ${NAME}
cp jpdfbookmarks_cli.exe ${NAME}               
# if the script is run from cygwin cp cannot write jpdfbookmarks.exe and 
# jpdfbookamarks in the same folder, we use the native windows xcopy
if [ -e /usr/bin/cygcheck ] 
then
	xcopy jpdfbookmarks ${NAME}
	xcopy jpdfbookmarks_cli ${NAME}
else 
 	cp jpdfbookmarks ${NAME}
	cp jpdfbookmarks_cli ${NAME}
fi
cp link_this_in_linux_path.sh ${NAME}
cp link_this_in_linux_path_cli.sh ${NAME}
cp ../jpdfbookmarks_core/dist/jpdfbookmarks.jar ${NAME}
cp ../README ${NAME}
cp ../COPYING ${NAME}
mkdir ${NAME}/lib
cp ../jpdfbookmarks_core/dist/lib/* ${NAME}/lib
cp ../jpdfbookmarks_graphics/artwork/jpdfbookmarks.png ${NAME}

zip -r ${NAME}.zip ${NAME}
tar -cpvzf ${NAME}.tar.gz ${NAME}     

rm -f ${NAME}.tar
rm -f -R ${NAME}

svn export .. ${SRCNAME}

zip -r ${SRCNAME}.zip ${SRCNAME}
tar -cpvzf ${SRCNAME}.tar.gz ${SRCNAME}      
      
rm -f -R ${SRCNAME}

cd ${PREV_DIR}
