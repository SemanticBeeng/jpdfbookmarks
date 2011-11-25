#!/bin/sh

#
# createtag.sh
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

svn copy https://jpdfbookmarks.svn.sourceforge.net/svnroot/jpdfbookmarks/trunk \
	https://jpdfbookmarks.svn.sourceforge.net/svnroot/jpdfbookmarks/tags/${VERSION} \
	-m "Tagging the ${VERSION} relase of the 'JPdfBookmarks' project"

cd ${PREV_DIR}
