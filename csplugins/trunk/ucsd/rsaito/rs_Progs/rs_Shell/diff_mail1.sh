#!/bin/sh

MAIL_COM=/usr/bin/mail # メールコマンドの絶対パス

WEB_ACCESS_LOG=./tmplog                         # WEBのアクセスログ。/var/log/httpd/access_logなど。
WEB_ACCESS_LOG_FILT=./test_grep                 # 関連があるログだけを抽出したファイル
WEB_ACCESS_LOG_FILT_PREV=./test_grep_diff_prev  # 前回関連があるログだけを抽出したファイル
WEB_ACCESS_LOG_FILT_DIFF=./test_grep_diff       # 前回との違い

MAIL_ADDRESS="golgo8028@yahoo.co.jp"
MAIL_SUBJECT="WEB_RS Access Report on `date`"
FILT_KEYWORD="WEB_RS"

grep ${FILT_KEYWORD} ${WEB_ACCESS_LOG} > ${WEB_ACCESS_LOG_FILT}
touch ${WEB_ACCESS_LOG_FILT_PREV}
diff ${WEB_ACCESS_LOG_FILT} ${WEB_ACCESS_LOG_FILT_PREV} | grep "^<" > ${WEB_ACCESS_LOG_FILT_DIFF}
mv ${WEB_ACCESS_LOG_FILT} ${WEB_ACCESS_LOG_FILT_PREV}

if [ -s ${WEB_ACCESS_LOG_FILT_DIFF} ]
then
   # ${MAIL_COM} -s "MAIL_SUBJECT" ${WEB_ACCESS_LOG_FILT_DIFF}
   echo ${MAIL_SUBJECT}
   cat ${WEB_ACCESS_LOG_FILT_DIFF}
fi

