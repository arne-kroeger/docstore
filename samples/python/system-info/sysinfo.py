from psutil import virtual_memory, cpu_count, cpu_freq, swap_memory, disk_partitions, disk_usage, net_if_addrs
import json
import platform
import docstore_python_client
from pprint import pprint
from docstore_python_client.rest import ApiException

configuration = docstore_python_client.Configuration(
    host = "https://api.docstore.dev"
)

# PLATFORM
uname = platform.uname()

# MEMORY
mem = virtual_memory()

# SWAP
swap = swap_memory().total

# CPU
physicalCors = cpu_count(logical=False)
logicalCors = cpu_count(logical=True)
maxFrequence = cpu_freq().max

#DISK
partitions = disk_partitions()
partitionInfo = []
for partition in partitions:
    try:
        partition_usage = disk_usage(partition.mountpoint)
    except PermissionError:
        # this can be catched due to the disk that
        # isn't ready
        continue
    partitionInfo.append({
        "Device": partition.device,
        "Mountpoint": partition.mountpoint,
        "FSType": partition.fstype,
        "Size-Total": partition_usage.total / 1024**3,
    })

# INTERFACES
if_addrs = net_if_addrs()
interaceInfo = []
for interface_name, interface_addresses in if_addrs.items():
    addresses = []
    for address in interface_addresses:
        addresses.append({
            "Address": address.address,
            "Netmask": address.netmask,
            "Broadcast": address.broadcast
        })

    interaceInfo.append({
        "Name": interface_name,
        "Addresses": addresses,
    })

#BUILD Structure
data_set = [docstore_python_client.TemplateVariable('data', {
    "Plattform": {
        "System": uname.system,
        "Name": uname.node,
        "Release": uname.release,
        "Version": uname.version,
        "Machine": uname.machine,
        "Processor": uname.processor,
    },
    "Memory": {
        "Total": mem.total/1024**3,
    },
    "Swap": {
        "Total": swap/1024**3,
    },
    "CPU": {
        "Cores": {
            "Physical": physicalCors,
            "Logical": logicalCors,
        },
        "Frequence": maxFrequence / 1000,
    },
    "Disk": {
        "Partitions": partitionInfo,
    },
    "Interfaces": interaceInfo
})]

# SEND DATA
with docstore_python_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = docstore_python_client.DocumentApi(api_client)
    templateData = docstore_python_client.TemplateData("system-report", docstore_python_client.Space("System"), docstore_python_client.Environment(uname.node), [
        "System", "Report", "Memory", "CPU", "Network", uname.node
    ], data_set) # Document | 
    
    try:
        # adds a new document
        api_response = api_instance.add_new_document_for_template("system-report", templateData)
        pprint(api_response)
    except ApiException as e:
        print("Exception when calling DocumentApi->add_new_document: %s\n" % e)