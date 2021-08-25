package user

import "github.com/gin-gonic/gin"

func QueryUserV1(c *gin.Context) {
	c.JSON(200, gin.H{
		"language": "go",
		"type": "application",
		"version": "v1",
		"user": "demo_v1",
	})
}

func QueryUserV2(c *gin.Context) {
	c.JSON(200, gin.H{
		"language": "go",
		"type": "application",
		"version": "v2",
		"user": "demo_v2",
	})
}
