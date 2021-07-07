module.exports = {
    context: __dirname + '/src/main/resources/static',
    entry: {
        main: './js/main.jsx',
    },
    output: {
        path: __dirname + '/target/classes/static',
        filename: "bundle.js"
    },
    module: {
        rules: [
            {
                test: /\.(js|jsx)$/,
                exclude: /(node_modules)/,
                loader: 'babel-loader',
                options: {
                    presets: ['@babel/preset-env']
                }
            },
            {
                test: /\.(css|scss)$/,
                use: ["style-loader", "css-loader"],
            }
        ]
    }
};