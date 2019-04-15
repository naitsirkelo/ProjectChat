#!/bin/bash
git add .
read -r -p "Commit description: " desc
git commit -m "$desc"
git push
