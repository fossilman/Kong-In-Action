local cjson = require "cjson"
local res = ngx.location.capture('/query')
local oldbody = res.body
local table1 = cjson.decode(oldbody)
local ip = {ngx.var.remote_addr}
for k in pairs(table1) do
    v = table1[k]
    if type(v) == "table" then
        for k2 in pairs(table1[k]) do
	    if table1[k][k2] == ip[1] then
		ngx.status = 200
		ngx.say("obtain OK")
		ngx.exit(ngx.OK)
	    end
	end
    end
end
ngx.status = 403
ngx.say("You IP address is not allowed")
ngx.exit(ngx.OK)
