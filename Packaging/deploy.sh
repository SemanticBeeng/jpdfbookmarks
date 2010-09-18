#!/bin/sh

#
# deploy.sh
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

DIRNAME=JPdfBookmarks-${VERSION}

rsync -avP -e ssh ${NAME}.zip ${NAME}.tar.gz ${SRCNAME}.zip ${SRCNAME}.tar.gz \
	fla1257,jpdfbookmarks@frs.sourceforge.net:/home/frs/project/j/jp/jpdfbookmarks/${DIRNAME}/

cd ${PREV_DIR}
