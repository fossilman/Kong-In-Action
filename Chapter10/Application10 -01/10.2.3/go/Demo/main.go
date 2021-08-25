package main

import (
	"demo/router"

	"github.com/gin-gonic/gin"
)

func main() {
	app := gin.Default()
	router.Register(app)
	app.Run("127.0.0.1:8080")
}
