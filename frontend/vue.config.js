module.exports = {
    outputDir: 'target/dist',
    assetsDir: 'static',
    devServer: {
        port: 8089,
        proxy: {
            '^/images': {
                target: 'http://gallery-backend:8080',
                ws: true,
                changeOrigin: true
            },
            '^/auth': {
                target: 'http://gallery-backend:8080',
                ws: true,
                changeOrigin: true
            },
            '^/api/test': {
                target: 'http://gallery-backend:8080',
                ws: true,
                changeOrigin: true
            },
            '^/user':{
                target: 'http://gallery-backend:8080',
                ws: true,
                changeOrigin: true
            }
        }
    }
}
