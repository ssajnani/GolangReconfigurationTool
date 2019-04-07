#!/bin/bash
source "./assert.sh"
successful=0
total=0
echo "Creating namespace using dryRun"
result=$(curl -ss --header "Content-Type: application/json" \
	--request POST \
	--data "{\"filepath\":\"${1}/test_namespaces/testNamespace.yaml\", \"type\": \"Namespace\", \"dryRun\": \"All\"}" \
	http://localhost:4567/create)
total=$((total+1))
expected="Success"
assert_eq "\"$expected\"" "$result" 
if [ "$?" == 0 ]; then
    successful=$((successful+1))
    log_success "Test successful"
else 
    log_failure "Test failed"
fi
echo "Creating namespace"
result=$(curl -ss --header "Content-Type: application/json" \
	--request POST \
	--data "{\"filepath\":\"${1}/test_namespaces/testNamespace.yaml\", \"type\": \"Namespace\"}" \
	http://localhost:4567/create)
total=$((total+1))
expected="Success"
assert_eq "\"$expected\"" "$result" 
if [ "$?" == 0 ]; then
    successful=$((successful+1))
    log_success "Test successful"
else 
    log_failure "Test failed"
fi
sleep 2
echo "Creating namespace with existing name"
result=$(curl -ss --header "Content-Type: application/json" \
	--request POST \
	--data "{\"filepath\":\"${1}/test_namespaces/testNamespace.yaml\", \"type\": \"Namespace\"}" \
	http://localhost:4567/create)
total=$((total+1))
expected="Failed"
assert_eq "\"$expected\"" "$result" 
if [ "$?" == 0 ]; then
    successful=$((successful+1))
    log_success "Test successful"
else 
    log_failure "Test failed"
fi
./test_deployments/docker_pull.sh > /dev/null
echo "Creating deployment dryRun"
result=$(curl -ss --header "Content-Type: application/json" \
	--request POST \
	--data "{\"namespace\":\"test\", \"filepath\":\"${1}/test_deployments/nginx.yaml\", \"type\": \"Deployment\", \"dryRun\":\"All\"}" \
	http://localhost:4567/create_namespaced)
total=$((total+1))
expected="Success"
assert_eq "\"$expected\"" "$result" 
if [ "$?" == 0 ]; then
    successful=$((successful+1))
    log_success "Test successful"
else 
    log_failure "Test failed"
fi
echo "Creating deployment"
result=$(curl -ss --header "Content-Type: application/json" \
	--request POST \
	--data "{\"namespace\":\"test\", \"filepath\":\"${1}/test_deployments/nginx.yaml\", \"type\": \"Deployment\"}" \
	http://localhost:4567/create_namespaced)
total=$((total+1))
expected="Success"
assert_eq "\"$expected\"" "$result" 
if [ "$?" == 0 ]; then
    successful=$((successful+1))
    log_success "Test successful"
else 
    log_failure "Test failed"
fi
sleep 2
echo "Creating deployment with existing name"
result=$(curl -ss --header "Content-Type: application/json" \
	--request POST \
	--data "{\"namespace\":\"test\", \"filepath\":\"${1}/test_deployments/nginx.yaml\", \"type\": \"Deployment\"}" \
	http://localhost:4567/create_namespaced)
total=$((total+1))
expected="Success"
assert_eq "\"$expected\"" "$result" 
if [ "$?" != 0 ]; then
    successful=$((successful+1))
    log_success "Test successful"
else 
    log_failure "Test failed"
fi
echo "Update deployment"
result=$(curl -ss --header "Content-Type: application/json" \
	--request POST \
	--data '{"name":"nginx-deployment", "namespace":"test", "patchString":"{\"op\":\"replace\",\"path\":\"/spec/replicas\",\"value\": 3}", "type": "Deployment"}' \
	http://localhost:4567/update_namespaced)
total=$((total+1))
expected="Success"
assert_eq "\"$expected\"" "$result" 
if [ "$?" == 0 ]; then
    successful=$((successful+1))
    log_success "Test successful"
else 
    log_failure "Test failed"
fi
echo "Creating service dryRun"
result=$(curl -ss --header "Content-Type: application/json" \
	--request POST \
	--data "{\"namespace\":\"test\", \"filepath\":\"${1}/test_services/nginxService.yaml\", \"type\": \"Service\", \"dryRun\":\"All\"}" \
	http://localhost:4567/create_namespaced)
total=$((total+1))
expected="Success"
assert_eq "\"$expected\"" "$result" 
if [ "$?" == 0 ]; then
    successful=$((successful+1))
    log_success "Test successful"
else 
    log_failure "Test failed"
fi
echo "Creating service"
result=$(curl -ss --header "Content-Type: application/json" \
	--request POST \
	--data "{\"namespace\":\"test\", \"filepath\":\"${1}/test_services/nginxService.yaml\", \"type\": \"Service\"}" \
	http://localhost:4567/create_namespaced)
total=$((total+1))
expected="Success"
assert_eq "\"$expected\"" "$result" 
if [ "$?" == 0 ]; then
    successful=$((successful+1))
    log_success "Test successful"
else 
    log_failure "Test failed"
fi
sleep 2
echo "Creating service with existing name"
result=$(curl -ss --header "Content-Type: application/json" \
	--request POST \
	--data "{\"namespace\":\"test\", \"filepath\":\"${1}/test_services/nginxService.yaml\", \"type\": \"Service\"}" \
	http://localhost:4567/create_namespaced)
total=$((total+1))
expected="Success"
assert_eq "\"$expected\"" "$result" 
if [ "$?" != 0 ]; then
    successful=$((successful+1))
    log_success "Test successful"
else 
    log_failure "Test failed"
fi
echo "Update service"
result=$(curl -ss --header "Content-Type: application/json" \
	--request POST \
	--data '{"name":"my-service", "namespace":"test", "patchString":"{\"op\":\"add\",\"path\":\"/spec/selector\",\"value\": {\"hello\":\"yo\"}}", "type": "Service"}' \
	http://localhost:4567/update_namespaced)
total=$((total+1))
expected="Success"
assert_eq "\"$expected\"" "$result" 
if [ "$?" == 0 ]; then
    successful=$((successful+1))
    log_success "Test successful"
else 
    log_failure "Test failed"
fi
echo "Update service back to nginx"
result=$(curl -ss --header "Content-Type: application/json" \
	--request POST \
	--data '{"name":"my-service", "namespace":"test", "patchString":"{\"op\":\"add\",\"path\":\"/spec/selector\",\"value\": {\"app\":\"nginx\"}}", "type": "Service"}' \
	http://localhost:4567/update_namespaced)
total=$((total+1))
expected="Success"
assert_eq "\"$expected\"" "$result" 
if [ "$?" == 0 ]; then
    successful=$((successful+1))
    log_success "Test successful"
else 
    log_failure "Test failed"
fi
echo "Creating pod dryRun"
result=$(curl -ss --header "Content-Type: application/json" \
	--request POST \
	--data "{\"namespace\":\"test\", \"filepath\":\"${1}/test_pods/httpd.yaml\", \"type\": \"Pod\", \"dryRun\":\"All\"}" \
	http://localhost:4567/create_namespaced)
total=$((total+1))
expected="Success"
assert_eq "\"$expected\"" "$result" 
if [ "$?" == 0 ]; then
    successful=$((successful+1))
    log_success "Test successful"
else 
    log_failure "Test failed"
fi
echo "Creating pod"
result=$(curl -ss --header "Content-Type: application/json" \
	--request POST \
	--data "{\"namespace\":\"test\", \"filepath\":\"${1}/test_pods/httpd.yaml\", \"type\": \"Pod\"}" \
	http://localhost:4567/create_namespaced)
total=$((total+1))
expected="Success"
assert_eq "\"$expected\"" "$result" 
if [ "$?" == 0 ]; then
    successful=$((successful+1))
    log_success "Test successful"
else 
    log_failure "Test failed"
fi
sleep 2
echo "Creating pod with existing name"
result=$(curl -ss --header "Content-Type: application/json" \
	--request POST \
	--data "{\"namespace\":\"test\", \"filepath\":\"${1}/test_pods/httpd.yaml\", \"type\": \"Pod\"}" \
	http://localhost:4567/create_namespaced)
total=$((total+1))
expected="Success"
assert_eq "\"$expected\"" "$result" 
if [ "$?" != 0 ]; then
    successful=$((successful+1))
    log_success "Test successful"
else 
    log_failure "Test failed"
fi
echo "Update pod"
result=$(curl -ss --header "Content-Type: application/json" \
	--request POST \
	--data '{"name":"httpd-pod", "namespace":"test", "patchString":"{\"op\":\"replace\",\"path\":\"/spec/containers/0/image\",\"value\": \"redis:latest\"}", "type": "Pod"}' \
	http://localhost:4567/update_namespaced)
total=$((total+1))
expected="Success"
assert_eq "\"$expected\"" "$result" 
if [ "$?" == 0 ]; then
    successful=$((successful+1))
    log_success "Test successful"
else 
    log_failure "Test failed"
fi
echo "Deleting namespace"
result=$(curl -ss --header "Content-Type: application/json" \
	--request POST \
	--data "{\"name\":\"test\", \"type\": \"Namespace\"}" \
	http://localhost:4567/delete)
total=$((total+1))
expected="Success"
assert_eq "\"$expected\"" "$result" 
if [ "$?" == 0 ]; then
    successful=$((successful+1))
    log_success "Test successful"
else 
    log_failure "Test failed"
fi
echo "$successful tests passed out of $total"
