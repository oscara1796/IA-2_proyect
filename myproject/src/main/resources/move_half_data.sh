#!/bin/bash
N=$(ls | wc -l)
M=$((N/2))
ls | head -n $M | xargs -I {} mv {} ../test_legit_emails/
