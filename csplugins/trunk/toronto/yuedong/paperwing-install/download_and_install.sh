#!/bin/bash

pushd bundles

./install_jogl.sh
./checkout.sh
./patch.sh
./install.sh

popd

