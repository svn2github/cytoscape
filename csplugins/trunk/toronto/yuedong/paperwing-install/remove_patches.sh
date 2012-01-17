#!/bin/bash

pushd bundles

for i in patches/*.patch
do
	patch -R -r patch.rej -p0 -N <$i
done

./install.sh

popd

