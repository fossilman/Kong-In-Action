package base

import "github.com/gin-gonic/gin"

func Base(c *gin.Context) {
	c.JSON(200, gin.H{
		"language": "go",
		"type": "application callback",
	})
}
