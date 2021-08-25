package router

import (
	"demo/controller/user"
	"demo/controller/base"

	"github.com/gin-gonic/gin"
)

func Register(r *gin.Engine) {
	api := r.Group("/demo/api")
	{
		api.GET("/", base.Base)
		api.GET("/users/v1", user.QueryUserV1)
		api.GET("/users/v2", user.QueryUserV2)
	}
}
