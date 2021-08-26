package main

import (
	"demo/router"

	"github.com/gin-gonic/gin"
)

func main() {
	app := gin.Default()
	router.Register(app)
	app.Run("0.0.0.0:8080")
}
