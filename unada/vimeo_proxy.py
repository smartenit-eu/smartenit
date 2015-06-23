#
# Copyright (C) 2014 The SmartenIT consortium (http://www.smartenit.eu)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#         http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

import re
import os
import urllib2

download_url = 'http://192.168.40.1:8181/unada/rest/download/'
access_url = 'http://192.168.40.1:8181/unada/rest/access'
vimeo_pattern = re.compile('^(www.)?vimeo.com/(.+/)*\d{5,15}$')
stream_pattern = re.compile('[a-z0-9_-]+\.[a-z]+\.[a-z]+/([^?]+)/(.*\.mp4).*')
headers = {'Accept': 'text/plain',
           'Accept-Charset': 'utf-8;q=0.7,*;q=0.3',
           'Content-Type': 'text/plain'}

def request(context, flow):
    vimeo_url = flow.request.pretty_host(hostheader=True) + flow.request.path;
    print 'Redirecting request ' + vimeo_url
    
    if vimeo_pattern.match(vimeo_url):
        print 'Request ' + vimeo_url + ' matches vimeo pattern!'
        vimeo_id = vimeo_url.split('/')[-1]
        resp = urllib2.urlopen(download_url + vimeo_id)
        resp.read(0)
        resp.close()
        return

    if stream_pattern.match(vimeo_url):
        print 'Request ' + vimeo_url + ' matches streaming pattern!'
        video_file = stream_pattern.match(vimeo_url).group(1) + '/' + stream_pattern.match(vimeo_url).group(2)
        file_path = os.environ['HOME'] + '/unada/' + video_file
        req = urllib2.Request(access_url, file_path, headers)
        resp = urllib2.urlopen(req)
        exists = resp.read()

        if exists == 'true':
            print 'File ' + file_path + ' exists, will serve from local http server!'
            flow.request.url = 'http://192.168.40.1/unada' + video_file
            flow.request.host = '192.168.40.1'
            flow.request.port = 80
            flow.request.path = '/unada/' + video_file
            flow.request.scheme = 'http'
            flow.request.headers['Host'] = ['192.168.40.1']
            return

def responseheaders(context, flow):
    flow.response.stream = True
    return
