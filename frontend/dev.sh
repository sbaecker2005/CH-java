#!/bin/bash
# Inicia o frontend React (Vite) usando o Node.js embutido pelo Maven
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
export PATH="$DIR/node:$PATH"
"$DIR/node/node.exe" "$DIR/node_modules/vite/bin/vite.js"
