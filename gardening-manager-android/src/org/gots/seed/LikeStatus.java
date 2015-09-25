package org.gots.seed;

public class LikeStatus {
    int userLikeStatus = 0;

    int likesCount = 0;

    public int getLikesCount() {
        return likesCount;
    }

    public int getUserLikeStatus() {
        return userLikeStatus;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public void setUserLikeStatus(int userLikeStatus) {
        this.userLikeStatus = userLikeStatus;

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getLikesCount() + " Likes-" + getUserLikeStatus());
        return builder.toString();
    }
}
