local cjson = require "cjson"
local res = ngx.location.capture('/query?type=black')
local oldbody = res.body
local oldbodytable = cjson.decode(oldbody)
local ip = {ngx.var.remote_addr}
blacklist = {}
for i in pairs(oldbodytable) do
    iplist = oldbodytable[i]
    local blackip = iplist["address"]
    table.insert(blacklist, blackip)
end
for j in pairs(blacklist) do
    if blacklist[j] == ip[1] then
	ngx.exit(403)
	return 	ngx.eof()
    end
end
