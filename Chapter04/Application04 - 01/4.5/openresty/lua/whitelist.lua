local cjson = require "cjson"
local res = ngx.location.capture('/query?type=white')
local oldbody = res.body
local oldbodytable = cjson.decode(oldbody)
local ip = {ngx.var.remote_addr}
whitelist = {}
for i in pairs(oldbodytable) do
    iplist = oldbodytable[i]
    local whiteip = iplist["address"]
    table.insert(whitelist, whiteip)
end
for j in pairs(whitelist) do
    if whitelist[j] == ip[1] then
    else
	ngx.exit(403)
	return 	ngx.eof()
    end
end
