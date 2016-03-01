var source = require('vinyl-source-stream');
var buffer = require('vinyl-buffer');
var gulp = require('gulp');
var gutil = require('gulp-util');
var browserify = require('browserify');
var babelify = require('babelify');
var watchify = require('watchify');
var uglify = require('gulp-uglify');
var sourcemaps = require('gulp-sourcemaps');
var rename = require('gulp-rename');
var assign = require('object-assign');
var concat = require('gulp-concat');
var minifyCSS = require('gulp-minify-css');
var autoprefixer = require('gulp-autoprefixer');
var livereload = require('gulp-livereload');
var connect = require('gulp-connect');

var MAIN = './js/app.js';

gulp.task('watcher', ['connect', 'watch']);

gulp.task('connect', function () {
    connect.server({
        root: '.',
        fallback: 'frontend.html',
        livereload: true
    });
});

gulp.task('watch', function () {
    gulp.watch('css/**', ['css-build']);
    gulp.watch('js/**/*', ['js-dev-build']);
});

gulp.task('css-build', function () {
    return gulp.src('css/*.css')
        .pipe(autoprefixer({
            browsers: [
                '> 1%',
                'last 2 versions',
                'IE 11'
            ],
            cascade: false
        }))
        .pipe(sourcemaps.init({loadMaps: true}))
        .pipe(concat('style.min.css'))
        .pipe(minifyCSS())
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest('./out'));
});

gulp.task('js-build', function () {
    return browserify({
        debug: true,
        entries: [MAIN],
        transform: [babelify],
        paths: ['./js/']
    })
        .on('log', gutil.log)
        .on('error', gutil.log)
        .bundle()
        .pipe(source(MAIN))
        .pipe(buffer())
        .pipe(sourcemaps.init({loadMaps: true}))
        .pipe(uglify())
        .pipe(rename('./browser-bundle.min.js'))
        .pipe(sourcemaps.write('./'))
        .pipe(gulp.dest('./out'));
});

gulp.task('js-dev-build', function () {
    return browserify({
        debug: true,
        entries: [MAIN],
        transform: [babelify],
        paths: ['./js/']
    })
        .on('log', gutil.log)
        .on('error', gutil.log)
        .bundle()
        .pipe(source('browser-bundle.min.js'))
        .pipe(gulp.dest('./out'))
        .pipe(connect.reload());
});

gulp.task('default', ['css-build', 'js-build']);
