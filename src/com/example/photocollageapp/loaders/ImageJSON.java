package com.example.photocollageapp.loaders;


import java.util.List;

public  class ImageJSON {
    public List<ImageDataType> data;

    public class ImageDataType {
        private CommentType comments;
        private LikesType likes;
        public ImagesType images;
        public int getRaiting(){
          return  comments.count + likes.count;
        }
        public String getThumbnailUrl() {
            return images.thumbnail.url;
        }
    }

    private class ImagesType {
        public ImagetProper low_resolution;
        public ImagetProper thumbnail;
        public ImagetProper standard_resolution;

        private class ImagetProper {
            public String url;
            public int width;
            public int height;
        }
    }

    class CommentType {
        public int count;

    }

    class LikesType {
        public int count;
    }
}
