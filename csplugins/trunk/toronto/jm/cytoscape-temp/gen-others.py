#!/usr/bin/env python

sources = [
    ['commons-codec', 'commons-codec', '1.3'],
    ['net.sourceforge.collections', 'collections-generic', '4.01'],
    ['commons-httpclient', 'commons-httpclient', '3.1'],
    ['net.sf.opencsv', 'opencsv', '2.1'],
    ['colt', 'colt', '1.2.0'],
]

for source in sources:
    group, artifact, version = source
    group_path = group.replace('.', '/')
    print 'curl -O http://code.cytoscape.org/nexus/content/repositories/public/%s/%s/%s/%s-%s.jar' % (group_path, artifact, version, artifact, version)
    print 'bnd wrap %s-%s.jar' % (artifact, version)
    print '''mvn deploy:deploy-file -DgroupId=cytoscape-temp -DartifactId=%s -Dversion=%s -Dpackaging=jar -Dfile=%s-%s.bar -DrepositoryId=thirdparty -Durl=http://cytoscape.wodaklab.org/nexus/content/repositories/thirdparty/''' % (artifact, version, artifact, version)
